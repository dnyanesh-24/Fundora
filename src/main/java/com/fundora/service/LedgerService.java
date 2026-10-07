package com.fundora.service;

import com.fundora.dto.DebtRelationDTO;
import com.fundora.dto.ExpenseRequestDTO;
import com.fundora.dto.LedgerSummaryDTO;
import com.fundora.exception.LedgerException;
import com.fundora.exception.UserNotFoundException;
import com.fundora.model.*;
import com.fundora.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Module I, II & VI: Core Fundora Shared Ledger & Expense Splitting Engine
 * Features:
 * 1. Equal / Exact expense calculations
 * 2. Min-Cash-Flow Greedy Debt Simplification Algorithm ("Who Owes Whom")
 * 3. Real-time net debt balance computation
 */
@Service
public class LedgerService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseSplitRepository expenseSplitRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private JdbcLedgerDao jdbcLedgerDao;

    @Autowired
    @Qualifier("equalExpenseSplitter")
    private ExpenseSplitter equalExpenseSplitter;

    @Autowired
    @Qualifier("exactExpenseSplitter")
    private ExpenseSplitter exactExpenseSplitter;

    /**
     * Records a new expense and computes individual splits
     */
    @Transactional
    public Expense addExpense(ExpenseRequestDTO dto) {
        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new LedgerException("Group with ID " + dto.getGroupId() + " not found."));

        User paidBy = userRepository.findById(dto.getPaidByUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getPaidByUserId()));

        Expense expense = new Expense(
                group,
                paidBy,
                dto.getTitle(),
                dto.getAmount(),
                dto.getCategory(),
                dto.getSplitType() != null ? dto.getSplitType() : SplitType.EQUAL
        );

        // Determine participants (default to all group members if not specified)
        List<User> participants;
        if (dto.getParticipantUserIds() != null && !dto.getParticipantUserIds().isEmpty()) {
            participants = userRepository.findAllById(dto.getParticipantUserIds());
        } else {
            participants = new ArrayList<>(group.getMembers());
        }

        if (participants.isEmpty()) {
            throw new LedgerException("Expense must have at least one participant.");
        }

        // Module II: Strategy Pattern Selection for Splitting Logic
        ExpenseSplitter splitter = (dto.getSplitType() == SplitType.EXACT) 
                ? exactExpenseSplitter 
                : equalExpenseSplitter;

        List<ExpenseSplit> splits = splitter.calculateSplits(expense, participants, dto.getExactSplits());
        expense.setSplits(splits);

        return expenseRepository.save(expense);
    }

    /**
     * Calculates complete ledger summary for a group, including net balances and simplified debt graph
     */
    @Transactional(readOnly = true)
    public LedgerSummaryDTO getGroupLedger(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new LedgerException("Group with ID " + groupId + " not found."));

        List<Expense> expenses = expenseRepository.findByGroupIdOrderByExpenseDateDesc(groupId);
        List<Settlement> settlements = settlementRepository.findByGroupIdOrderBySettledAtDesc(groupId);

        // Calculate total group spending
        BigDecimal totalSpending = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Module III: Using JDBC DAO for optimized aggregate net balances
        Map<Long, BigDecimal> netBalances = jdbcLedgerDao.calculateRawUserBalances(groupId);

        // Ensure all group members have an entry in net balances
        for (User member : group.getMembers()) {
            netBalances.putIfAbsent(member.getId(), BigDecimal.ZERO);
        }

        // Module I & VI: Min-Cash-Flow Greedy Debt Simplification
        List<DebtRelationDTO> simplifiedDebts = simplifyDebts(netBalances, group);

        return new LedgerSummaryDTO(group, totalSpending, netBalances, simplifiedDebts, expenses, settlements);
    }

    /**
     * Greedy Min-Cash-Flow Debt Simplification Algorithm
     * Minimizes the total number of transactions required to settle all debts in a group.
     */
    private List<DebtRelationDTO> simplifyDebts(Map<Long, BigDecimal> netBalances, Group group) {
        List<DebtRelationDTO> debtList = new ArrayList<>();
        Map<Long, User> userMap = group.getMembers().stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // Priority Queues for Debtors (negative balance) and Creditors (positive balance)
        // Debtor balance stored as positive amount owed for easy comparison
        class BalanceNode {
            Long userId;
            BigDecimal amount;

            BalanceNode(Long userId, BigDecimal amount) {
                this.userId = userId;
                this.amount = amount;
            }
        }

        PriorityQueue<BalanceNode> debtors = new PriorityQueue<>((a, b) -> b.amount.compareTo(a.amount));
        PriorityQueue<BalanceNode> creditors = new PriorityQueue<>((a, b) -> b.amount.compareTo(a.amount));

        for (Map.Entry<Long, BigDecimal> entry : netBalances.entrySet()) {
            BigDecimal balance = entry.getValue().setScale(2, RoundingMode.HALF_UP);
            if (balance.compareTo(new BigDecimal("0.01")) >= 0) {
                creditors.offer(new BalanceNode(entry.getKey(), balance));
            } else if (balance.compareTo(new BigDecimal("-0.01")) <= 0) {
                debtors.offer(new BalanceNode(entry.getKey(), balance.abs()));
            }
        }

        while (!debtors.isEmpty() && !creditors.isEmpty()) {
            BalanceNode debtor = debtors.poll();
            BalanceNode creditor = creditors.poll();

            BigDecimal settlementAmount = debtor.amount.min(creditor.amount);

            User debtorUser = userMap.get(debtor.userId);
            User creditorUser = userMap.get(creditor.userId);

            if (debtorUser != null && creditorUser != null) {
                debtList.add(new DebtRelationDTO(
                        debtorUser.getId(),
                        debtorUser.getName(),
                        creditorUser.getId(),
                        creditorUser.getName(),
                        creditorUser.getUpiId(),
                        settlementAmount
                ));
            }

            BigDecimal remainingDebtor = debtor.amount.subtract(settlementAmount);
            BigDecimal remainingCreditor = creditor.amount.subtract(settlementAmount);

            if (remainingDebtor.compareTo(new BigDecimal("0.01")) >= 0) {
                debtors.offer(new BalanceNode(debtor.userId, remainingDebtor));
            }
            if (remainingCreditor.compareTo(new BigDecimal("0.01")) >= 0) {
                creditors.offer(new BalanceNode(creditor.userId, remainingCreditor));
            }
        }

        return debtList;
    }
}

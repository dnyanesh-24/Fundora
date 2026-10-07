package com.fundora.dto;

import com.fundora.model.Expense;
import com.fundora.model.Group;
import com.fundora.model.Settlement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Module I & VI: Aggregate DTO representing the entire group ledger state
 */
public class LedgerSummaryDTO {
    private Group group;
    private BigDecimal totalGroupSpending;
    private Map<Long, BigDecimal> netBalances;
    private List<DebtRelationDTO> simplifiedDebts;
    private List<Expense> recentExpenses;
    private List<Settlement> recentSettlements;

    public LedgerSummaryDTO() {}

    public LedgerSummaryDTO(Group group, BigDecimal totalGroupSpending, Map<Long, BigDecimal> netBalances,
                            List<DebtRelationDTO> simplifiedDebts, List<Expense> recentExpenses, List<Settlement> recentSettlements) {
        this.group = group;
        this.totalGroupSpending = totalGroupSpending;
        this.netBalances = netBalances;
        this.simplifiedDebts = simplifiedDebts;
        this.recentExpenses = recentExpenses;
        this.recentSettlements = recentSettlements;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public BigDecimal getTotalGroupSpending() {
        return totalGroupSpending;
    }

    public void setTotalGroupSpending(BigDecimal totalGroupSpending) {
        this.totalGroupSpending = totalGroupSpending;
    }

    public Map<Long, BigDecimal> getNetBalances() {
        return netBalances;
    }

    public void setNetBalances(Map<Long, BigDecimal> netBalances) {
        this.netBalances = netBalances;
    }

    public List<DebtRelationDTO> getSimplifiedDebts() {
        return simplifiedDebts;
    }

    public void setSimplifiedDebts(List<DebtRelationDTO> simplifiedDebts) {
        this.simplifiedDebts = simplifiedDebts;
    }

    public List<Expense> getRecentExpenses() {
        return recentExpenses;
    }

    public void setRecentExpenses(List<Expense> recentExpenses) {
        this.recentExpenses = recentExpenses;
    }

    public List<Settlement> getRecentSettlements() {
        return recentSettlements;
    }

    public void setRecentSettlements(List<Settlement> recentSettlements) {
        this.recentSettlements = recentSettlements;
    }
}

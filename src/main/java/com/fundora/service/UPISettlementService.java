package com.fundora.service;

import com.fundora.dto.SettlementRequestDTO;
import com.fundora.exception.LedgerException;
import com.fundora.exception.UserNotFoundException;
import com.fundora.model.Group;
import com.fundora.model.Settlement;
import com.fundora.model.User;
import com.fundora.repository.GroupRepository;
import com.fundora.repository.SettlementRepository;
import com.fundora.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Module II & VI: UPI Settlement Service implementing PaymentGatewayInterface
 * Automates UPI transaction recordation and auto-balances the shared ledger.
 */
@Service
public class UPISettlementService implements PaymentGatewayInterface {

    private static final Pattern UPI_PATTERN = Pattern.compile("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$");

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean validateVPA(String upiId) {
        if (upiId == null || upiId.trim().isEmpty()) {
            return false;
        }
        return UPI_PATTERN.matcher(upiId.trim()).matches();
    }

    @Override
    @Transactional
    public Settlement processUPISettlement(Long groupId, User payer, User payee, BigDecimal amount, String upiId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new LedgerException("Settlement amount must be greater than zero.");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new LedgerException("Group not found with ID: " + groupId));

        // Generate synthetic UPI Reference ID (e.g., UPI/2026/894723984)
        String txnRef = "UPI/FUNDORA/" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Settlement settlement = new Settlement(
                group,
                payer,
                payee,
                amount,
                "UPI",
                txnRef
        );
        settlement.setSettlementStatus("COMPLETED");
        settlement.setSettledAt(LocalDateTime.now());

        // Update in-app wallet balances
        payer.debitWallet(amount);
        payee.creditWallet(amount);
        userRepository.save(payer);
        userRepository.save(payee);

        return settlementRepository.save(settlement);
    }

    @Transactional
    public Settlement recordSettlement(SettlementRequestDTO dto) {
        User payer = userRepository.findById(dto.getPayerUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getPayerUserId()));
        User payee = userRepository.findById(dto.getPayeeUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getPayeeUserId()));

        return processUPISettlement(dto.getGroupId(), payer, payee, dto.getAmount(), dto.getUpiId());
    }
}

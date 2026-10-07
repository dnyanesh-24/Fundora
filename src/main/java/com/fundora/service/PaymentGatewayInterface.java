package com.fundora.service;

import com.fundora.model.Settlement;
import com.fundora.model.User;
import java.math.BigDecimal;

/**
 * Module II (Interfaces): Payment gateway abstraction for UPI, Wallets, and NetBanking
 */
public interface PaymentGatewayInterface {

    /**
     * Executes UPI peer settlement
     */
    Settlement processUPISettlement(Long groupId, User payer, User payee, BigDecimal amount, String upiId);

    /**
     * Validates UPI VPA ID format (e.g. name@bank)
     */
    boolean validateVPA(String upiId);
}

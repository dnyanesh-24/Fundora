package com.fundora.exception;

/**
 * Module II: Exception thrown when wallet or payment balance is insufficient
 */
public class InsufficientBalanceException extends LedgerException {

    public InsufficientBalanceException(String message) {
        super("INSUFFICIENT_BALANCE", message);
    }
}

package com.fundora.exception;

/**
 * Module II: Exception thrown when split amounts don't sum to the total expense amount
 */
public class LedgerMismatchException extends LedgerException {

    public LedgerMismatchException(String message) {
        super("LEDGER_MISMATCH", message);
    }
}

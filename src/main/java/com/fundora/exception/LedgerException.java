package com.fundora.exception;

/**
 * Module II (Inheritance & Exception Handling): Base application exception
 */
public class LedgerException extends RuntimeException {

    private final String errorCode;

    public LedgerException(String message) {
        super(message);
        this.errorCode = "LEDGER_ERR_GENERIC";
    }

    public LedgerException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public LedgerException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "LEDGER_ERR_CAUSE";
    }

    public String getErrorCode() {
        return errorCode;
    }
}

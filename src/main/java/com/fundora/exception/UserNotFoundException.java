package com.fundora.exception;

/**
 * Module II: Exception thrown when a user is not found in database or group
 */
public class UserNotFoundException extends LedgerException {

    public UserNotFoundException(String message) {
        super("USER_NOT_FOUND", message);
    }

    public UserNotFoundException(Long userId) {
        super("USER_NOT_FOUND", "User with ID " + userId + " was not found in the Fundora network.");
    }
}

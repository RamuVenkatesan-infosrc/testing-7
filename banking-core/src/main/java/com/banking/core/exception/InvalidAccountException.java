package com.banking.core.exception;

/**
 * Exception thrown when an account operation is attempted on an invalid account.
 */
public class InvalidAccountException extends RuntimeException {
    public InvalidAccountException(String message) {
        super(message);
    }
}


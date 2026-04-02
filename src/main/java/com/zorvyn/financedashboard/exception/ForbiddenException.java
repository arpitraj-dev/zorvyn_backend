package com.zorvyn.financedashboard.exception;

/**
 * Exception thrown when user does not have permission to perform an action.
 * Returns 403 Forbidden.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}

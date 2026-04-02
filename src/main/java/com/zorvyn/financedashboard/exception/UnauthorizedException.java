package com.zorvyn.financedashboard.exception;

/**
 * Exception thrown when user authentication fails (user not found).
 * Returns 401 Unauthorized.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}

package com.zorvyn.financedashboard.exception;

/**
 * Exception thrown for bad request errors (400).
 * Use for invalid input that doesn't fit other specific exceptions.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

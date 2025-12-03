package ca.umanitoba.cs.oluwasef.exceptions;

/**
 * Base type for all validation and business-rule errors that should be
 * shown to users without a stack trace.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

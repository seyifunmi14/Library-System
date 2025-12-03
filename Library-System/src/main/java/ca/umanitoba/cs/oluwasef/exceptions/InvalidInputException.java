package ca.umanitoba.cs.oluwasef.exceptions;

/**
 * Thrown when user-provided input is syntactically invalid or missing.
 */
public class InvalidInputException extends ValidationException {
    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}

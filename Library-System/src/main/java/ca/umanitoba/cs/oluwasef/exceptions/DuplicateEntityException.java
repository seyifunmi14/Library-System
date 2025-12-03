package ca.umanitoba.cs.oluwasef.exceptions;

/**
 * Thrown when an entity that should be unique already exists.
 */
public class DuplicateEntityException extends ValidationException {
    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}

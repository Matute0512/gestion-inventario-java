package io.github.torres.exception;

/**
 * Custom exception for validation errors in application.
 *
 * This exception is thrown user input or data validation fails.
 * It provides a user-friendly error message that can be displayed in dialogs.
 *
 * <p>
 *     Usage:
 *     <pre>
 *         if (price < 0){
 *             throw new ValidationException("El precio no puede ser negativo.");
 *         }
 *     </pre>
 * </p>
 *
 * @author Matias
 * @version 1.0
 */
public class ValidationException extends Exception {

    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     *
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ValidationException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ValidationException with the specified cause and a detail message
     * of (cause==null ? null : cause.toString()).
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     *        (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }

}

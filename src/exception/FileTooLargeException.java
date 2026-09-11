package exception;

/**
 * Thrown when an uploaded file exceeds the maximum size allowed by the destination country.
 */
public class FileTooLargeException extends DocumentProcessingException {
    public FileTooLargeException(String message) {
        super(message);
    }
}

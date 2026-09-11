package exception;

/**
 * Thrown when an uploaded file has no content (0 bytes).
 */
public class EmptyDocumentException extends DocumentProcessingException {
    public EmptyDocumentException(String message) {
        super(message);
    }
}

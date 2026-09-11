package exception;

/**
 * Base checked exception for every error that can occur while validating or processing
 * a document. Kept checked on purpose so that every code path handling a document is
 * forced to decide how to react to a failure, instead of letting it crash the batch.
 */
public class DocumentProcessingException extends Exception {

    public DocumentProcessingException(String message) {
        super(message);
    }

    public DocumentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

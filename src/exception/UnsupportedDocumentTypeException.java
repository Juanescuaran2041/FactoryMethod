package exception;

/**
 * Thrown when the requested document type is not one of the five categories GlobalDocs handles.
 */
public class UnsupportedDocumentTypeException extends DocumentProcessingException {
    public UnsupportedDocumentTypeException(String message) {
        super(message);
    }
}

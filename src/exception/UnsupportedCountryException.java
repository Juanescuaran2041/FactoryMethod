package exception;

/**
 * Thrown when a document references a country GlobalDocs does not currently operate in.
 */
public class UnsupportedCountryException extends DocumentProcessingException {
    public UnsupportedCountryException(String message) {
        super(message);
    }
}

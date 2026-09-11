package exception;

/**
 * Thrown when a document fails a country-specific regulatory rule: the document type is
 * not accepted in that country, or a fiscal/tax identifier does not match the expected format.
 */
public class CountryComplianceException extends DocumentProcessingException {
    public CountryComplianceException(String message) {
        super(message);
    }
}

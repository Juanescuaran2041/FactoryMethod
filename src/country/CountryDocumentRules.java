package country;

import model.DocumentType;

/**
 * Product of the {@link CountryComplianceFactory} abstract factory: describes which document
 * types a country accepts and the maximum upload size it allows.
 */
public interface CountryDocumentRules {

    boolean isDocumentTypeSupported(DocumentType documentType);

    long getMaxFileSizeBytes();

    /** Human-readable list of the document types this country accepts, for messages. */
    String describeSupportedDocumentTypes();
}

package country;

import model.DocumentType;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reusable {@link CountryDocumentRules} implementation configured with the set of document
 * types accepted in one country and its maximum upload size.
 */
public final class StandardCountryDocumentRules implements CountryDocumentRules {

    private final Set<DocumentType> supportedDocumentTypes;
    private final long maxFileSizeBytes;

    public StandardCountryDocumentRules(Set<DocumentType> supportedDocumentTypes, long maxFileSizeBytes) {
        this.supportedDocumentTypes = EnumSet.copyOf(supportedDocumentTypes);
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    @Override
    public boolean isDocumentTypeSupported(DocumentType documentType) {
        return supportedDocumentTypes.contains(documentType);
    }

    @Override
    public long getMaxFileSizeBytes() {
        return maxFileSizeBytes;
    }

    @Override
    public String describeSupportedDocumentTypes() {
        return supportedDocumentTypes.stream()
                .map(DocumentType::getDisplayName)
                .collect(Collectors.joining(", "));
    }
}

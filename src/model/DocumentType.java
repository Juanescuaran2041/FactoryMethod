package model;

/**
 * Business document categories handled by the GlobalDocs processing platform.
 */
public enum DocumentType {
    ELECTRONIC_INVOICE("Electronic Invoice"),
    LEGAL_CONTRACT("Legal Contract"),
    FINANCIAL_REPORT("Financial Report"),
    DIGITAL_CERTIFICATE("Digital Certificate"),
    TAX_RETURN("Tax Return");

    private final String displayName;

    DocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DocumentType fromCode(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            throw new IllegalArgumentException("Document type must not be empty");
        }
        String normalized = rawCode.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        for (DocumentType type : values()) {
            if (type.name().equals(normalized)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown document type: " + rawCode);
    }
}

package model;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable representation of a document that has already passed format, size and
 * country-compliance validation and is ready to be handed to a {@link processor.DocumentProcessor}.
 */
public final class Document {

    private final String id;
    private final String fileName;
    private final DocumentType documentType;
    private final DocumentFormat format;
    private final CountryCode countryCode;
    private final byte[] content;
    private final String taxId;
    private final Instant receivedAt;

    public Document(String fileName,
                     DocumentType documentType,
                     DocumentFormat format,
                     CountryCode countryCode,
                     byte[] content,
                     String taxId) {
        this.id = UUID.randomUUID().toString();
        this.fileName = fileName;
        this.documentType = documentType;
        this.format = format;
        this.countryCode = countryCode;
        this.content = content;
        this.taxId = taxId;
        this.receivedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public DocumentFormat getFormat() {
        return format;
    }

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public byte[] getContent() {
        return content;
    }

    public long getSizeBytes() {
        return content == null ? 0L : content.length;
    }

    public String getTaxId() {
        return taxId;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}

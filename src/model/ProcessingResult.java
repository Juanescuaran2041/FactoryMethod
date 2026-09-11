package model;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Outcome of processing a single document. Always produced, never thrown, so that
 * a single failing document never interrupts a batch.
 */
public final class ProcessingResult {

    private final String resultId;
    private final String fileName;
    private final String requestedDocumentType;
    private final String requestedCountry;
    private final ProcessingStatus status;
    private final String message;
    private final Instant processedAt;
    private final Map<String, String> metadata;

    private ProcessingResult(String fileName,
                              String requestedDocumentType,
                              String requestedCountry,
                              ProcessingStatus status,
                              String message,
                              Map<String, String> metadata) {
        this.resultId = UUID.randomUUID().toString();
        this.fileName = fileName;
        this.requestedDocumentType = requestedDocumentType;
        this.requestedCountry = requestedCountry;
        this.status = status;
        this.message = message;
        this.processedAt = Instant.now();
        this.metadata = metadata == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(metadata));
    }

    public static ProcessingResult success(String fileName,
                                            String requestedDocumentType,
                                            String requestedCountry,
                                            String message,
                                            Map<String, String> metadata) {
        return new ProcessingResult(fileName, requestedDocumentType, requestedCountry,
                ProcessingStatus.SUCCESS, message, metadata);
    }

    public static ProcessingResult rejected(String fileName,
                                             String requestedDocumentType,
                                             String requestedCountry,
                                             String message) {
        return new ProcessingResult(fileName, requestedDocumentType, requestedCountry,
                ProcessingStatus.REJECTED, message, null);
    }

    public static ProcessingResult failed(String fileName,
                                           String requestedDocumentType,
                                           String requestedCountry,
                                           String message) {
        return new ProcessingResult(fileName, requestedDocumentType, requestedCountry,
                ProcessingStatus.FAILED, message, null);
    }

    public String getResultId() {
        return resultId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getRequestedDocumentType() {
        return requestedDocumentType;
    }

    public String getRequestedCountry() {
        return requestedCountry;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}

package batch;

import model.BatchResult;
import model.ProcessingResult;
import service.DocumentProcessingService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes a batch of documents (GlobalDocs handles 50,000+ per day) as independent units:
 * every item is run through {@link DocumentProcessingService}, which never throws, so a
 * single malformed or non-compliant file is recorded as a failed/rejected result without
 * stopping the rest of the batch.
 */
public final class BatchProcessor {

    private final DocumentProcessingService processingService = new DocumentProcessingService();

    public BatchResult processBatch(List<BatchUploadItem> items, String countryCodeRaw, String taxId) {
        Instant startedAt = Instant.now();
        List<ProcessingResult> results = new ArrayList<>(items.size());
        for (BatchUploadItem item : items) {
            ProcessingResult result = processingService.processUpload(
                    item.getFileName(), item.getContent(), item.getDocumentTypeRaw(), countryCodeRaw, taxId);
            results.add(result);
        }
        Instant finishedAt = Instant.now();
        return new BatchResult(startedAt, finishedAt, results);
    }
}

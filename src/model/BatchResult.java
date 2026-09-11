package model;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregated outcome of processing a batch of documents in a single request.
 */
public final class BatchResult {

    private final String batchId;
    private final Instant startedAt;
    private final Instant finishedAt;
    private final List<ProcessingResult> results;

    public BatchResult(Instant startedAt, Instant finishedAt, List<ProcessingResult> results) {
        this.batchId = UUID.randomUUID().toString();
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.results = Collections.unmodifiableList(results);
    }

    public String getBatchId() {
        return batchId;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public List<ProcessingResult> getResults() {
        return results;
    }

    public int getTotalCount() {
        return results.size();
    }

    public long countByStatus(ProcessingStatus status) {
        return results.stream().filter(r -> r.getStatus() == status).count();
    }
}

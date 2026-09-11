package web;

import model.BatchResult;
import model.ProcessingResult;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Hand-rolled JSON serialization for the small, well-known response shapes this API
 * returns. Kept dependency-free on purpose, matching the rest of the backend.
 */
public final class JsonWriter {

    private JsonWriter() {
    }

    public static String toJson(ProcessingResult result) {
        StringBuilder sb = new StringBuilder();
        writeResult(sb, result);
        return sb.toString();
    }

    public static String toJson(BatchResult batchResult) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        field(sb, "batchId", batchResult.getBatchId());
        sb.append(',');
        field(sb, "startedAt", DateTimeFormatter.ISO_INSTANT.format(batchResult.getStartedAt()));
        sb.append(',');
        field(sb, "finishedAt", DateTimeFormatter.ISO_INSTANT.format(batchResult.getFinishedAt()));
        sb.append(',');
        sb.append("\"totalCount\":").append(batchResult.getTotalCount());
        sb.append(',');
        sb.append("\"successCount\":").append(batchResult.countByStatus(model.ProcessingStatus.SUCCESS));
        sb.append(',');
        sb.append("\"rejectedCount\":").append(batchResult.countByStatus(model.ProcessingStatus.REJECTED));
        sb.append(',');
        sb.append("\"failedCount\":").append(batchResult.countByStatus(model.ProcessingStatus.FAILED));
        sb.append(',');
        sb.append("\"results\":[");
        List<ProcessingResult> results = batchResult.getResults();
        for (int i = 0; i < results.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            writeResult(sb, results.get(i));
        }
        sb.append(']');
        sb.append('}');
        return sb.toString();
    }

    private static void writeResult(StringBuilder sb, ProcessingResult result) {
        sb.append('{');
        field(sb, "resultId", result.getResultId());
        sb.append(',');
        field(sb, "fileName", result.getFileName());
        sb.append(',');
        field(sb, "requestedDocumentType", result.getRequestedDocumentType());
        sb.append(',');
        field(sb, "requestedCountry", result.getRequestedCountry());
        sb.append(',');
        field(sb, "status", result.getStatus().name());
        sb.append(',');
        field(sb, "message", result.getMessage());
        sb.append(',');
        field(sb, "processedAt", DateTimeFormatter.ISO_INSTANT.format(result.getProcessedAt()));
        sb.append(',');
        sb.append("\"metadata\":{");
        boolean first = true;
        for (Map.Entry<String, String> entry : result.getMetadata().entrySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            field(sb, entry.getKey(), entry.getValue());
        }
        sb.append('}');
        sb.append('}');
    }

    public static void field(StringBuilder sb, String key, String value) {
        sb.append(quote(key)).append(':').append(quote(value));
    }

    public static String quote(String raw) {
        if (raw == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(raw.length() + 2);
        sb.append('"');
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}

package web;

import batch.BatchProcessor;
import batch.BatchUploadItem;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.BatchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * POST /api/documents/batch - real batch flow. The client appends, for every row the user
 * added, a "documentType" text part immediately followed by the matching "file" part (form
 * field append order is preserved on the wire), plus one shared "countryCode" (and optional
 * "taxId") for the whole batch. Every file is validated and processed independently by
 * {@link BatchProcessor}, so one unsupported file never blocks the rest of the batch.
 */
final class BatchUploadHandler implements HttpHandler {

    private final BatchProcessor batchProcessor = new BatchProcessor();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                HttpUtils.sendError(exchange, 405, "Only POST is supported on this endpoint.");
                return;
            }

            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            String boundary = MultipartParser.extractBoundary(contentType);
            if (boundary == null) {
                HttpUtils.sendError(exchange, 400, "Expected a multipart/form-data request with a boundary.");
                return;
            }

            byte[] body = HttpUtils.readFully(exchange.getRequestBody());
            List<MultipartParser.Part> parts = MultipartParser.parse(body, boundary);

            String countryCode = null;
            String taxId = null;
            String pendingDocumentType = null;
            List<BatchUploadItem> items = new ArrayList<>();

            for (MultipartParser.Part part : parts) {
                if ("countryCode".equals(part.name)) {
                    countryCode = part.asText();
                } else if ("taxId".equals(part.name)) {
                    taxId = part.asText();
                } else if ("documentType".equals(part.name)) {
                    pendingDocumentType = part.asText();
                } else if (part.isFile() && "file".equals(part.name)) {
                    items.add(new BatchUploadItem(part.filename, part.data, pendingDocumentType));
                }
            }

            if (items.isEmpty()) {
                HttpUtils.sendError(exchange, 400, "The batch did not contain any files.");
                return;
            }
            if (countryCode == null) {
                HttpUtils.sendError(exchange, 400, "\"countryCode\" is required for a batch upload.");
                return;
            }

            BatchResult result = batchProcessor.processBatch(items, countryCode, taxId);
            HttpUtils.sendJson(exchange, 200, JsonWriter.toJson(result));
        } catch (Exception e) {
            HttpUtils.sendError(exchange, 500, "Unexpected server error: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }
}

package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.ProcessingResult;
import service.DocumentProcessingService;

import java.io.IOException;
import java.util.List;

/**
 * POST /api/documents/process - the real single-document flow: the browser uploads the raw
 * file exactly as chosen by the user (no client-side filtering), and this backend is the one
 * that decides, through {@link DocumentProcessingService}, whether the format/country/type
 * combination is acceptable (e.g. a video file is rejected here, not hidden by the UI).
 */
final class ProcessDocumentHandler implements HttpHandler {

    private final DocumentProcessingService processingService = new DocumentProcessingService();

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

            String documentType = null;
            String countryCode = null;
            String taxId = null;
            MultipartParser.Part filePart = null;

            for (MultipartParser.Part part : parts) {
                if (part.isFile() && "file".equals(part.name)) {
                    filePart = part;
                } else if ("documentType".equals(part.name)) {
                    documentType = part.asText();
                } else if ("countryCode".equals(part.name)) {
                    countryCode = part.asText();
                } else if ("taxId".equals(part.name)) {
                    taxId = part.asText();
                }
            }

            if (filePart == null) {
                HttpUtils.sendError(exchange, 400, "No file was attached to the request (missing \"file\" field).");
                return;
            }
            if (documentType == null || countryCode == null) {
                HttpUtils.sendError(exchange, 400, "Both \"documentType\" and \"countryCode\" are required.");
                return;
            }

            ProcessingResult result = processingService.processUpload(
                    filePart.filename, filePart.data, documentType, countryCode, taxId);

            HttpUtils.sendJson(exchange, 200, JsonWriter.toJson(result));
        } catch (Exception e) {
            HttpUtils.sendError(exchange, 500, "Unexpected server error: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }
}

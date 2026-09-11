package processor;

import exception.DocumentProcessingException;
import exception.EmptyDocumentException;
import model.Document;
import model.DocumentFormat;
import model.ProcessingResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;


public abstract class AbstractDocumentProcessor implements DocumentProcessor {

    @Override
    public final ProcessingResult process(Document document) throws DocumentProcessingException {
        if (document.getContent() == null || document.getContent().length == 0) {
            throw new EmptyDocumentException("File \"" + document.getFileName() + "\" is empty (0 bytes).");
        }

        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("processingEngine", getEngineName());
        metadata.put("regulatoryFrameworkApplied", document.getCountryCode().getDisplayName());
        metadata.put("sizeBytes", String.valueOf(document.getSizeBytes()));
        metadata.put("checksumSha256", computeChecksum(document.getContent()));
        if (isTextLikeFormat(document.getFormat())) {
            metadata.put("estimatedWordCount", String.valueOf(estimateWordCount(document.getContent())));
        }
        metadata.putAll(extractTypeSpecificMetadata(document));

        String message = getSuccessMessage(document);
        return ProcessingResult.success(
                document.getFileName(),
                document.getDocumentType().getDisplayName(),
                document.getCountryCode().getDisplayName(),
                message,
                metadata);
    }

    /** Hook: type-specific simulated data extraction. */
    protected abstract Map<String, String> extractTypeSpecificMetadata(Document document);

    protected abstract String getEngineName();

    protected abstract String getSuccessMessage(Document document);

    private boolean isTextLikeFormat(DocumentFormat format) {
        return format == DocumentFormat.TXT || format == DocumentFormat.MD || format == DocumentFormat.CSV;
    }

    private int estimateWordCount(byte[] content) {
        String text = new String(content, StandardCharsets.UTF_8);
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }
        return trimmed.split("\\s+").length;
    }

    private String computeChecksum(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content);
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b & 0xFF));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available on this JVM.", e);
        }
    }
}

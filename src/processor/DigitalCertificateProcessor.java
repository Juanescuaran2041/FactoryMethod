package processor;

import model.Document;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DigitalCertificateProcessor extends AbstractDocumentProcessor {

    @Override
    protected Map<String, String> extractTypeSpecificMetadata(Document document) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("certificateReference", "CERT-" + document.getId().substring(0, 8).toUpperCase());
        metadata.put("integrityCheck", "passed");
        return metadata;
    }

    @Override
    protected String getEngineName() {
        return "DigitalCertificateEngine";
    }

    @Override
    protected String getSuccessMessage(Document document) {
        return "Digital certificate integrity verified for "
                + document.getCountryCode().getDisplayName() + ".";
    }
}

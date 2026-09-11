package processor;

import model.Document;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LegalContractProcessor extends AbstractDocumentProcessor {

    @Override
    protected Map<String, String> extractTypeSpecificMetadata(Document document) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("contractReference", "CTR-" + document.getId().substring(0, 8).toUpperCase());
        metadata.put("retentionPolicy", "7-year legal archive");
        return metadata;
    }

    @Override
    protected String getEngineName() {
        return "LegalContractEngine";
    }

    @Override
    protected String getSuccessMessage(Document document) {
        return "Legal contract archived under the legal framework of "
                + document.getCountryCode().getDisplayName() + ".";
    }
}

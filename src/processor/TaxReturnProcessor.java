package processor;

import model.Document;

import java.util.LinkedHashMap;
import java.util.Map;

public final class TaxReturnProcessor extends AbstractDocumentProcessor {

    @Override
    protected Map<String, String> extractTypeSpecificMetadata(Document document) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("filingReference", "TAX-" + document.getId().substring(0, 8).toUpperCase());
        if (document.getTaxId() != null && !document.getTaxId().isBlank()) {
            metadata.put("filerTaxId", document.getTaxId());
        }
        return metadata;
    }

    @Override
    protected String getEngineName() {
        return "TaxReturnEngine";
    }

    @Override
    protected String getSuccessMessage(Document document) {
        return "Tax return filed under the fiscal regulations of "
                + document.getCountryCode().getDisplayName() + ".";
    }
}

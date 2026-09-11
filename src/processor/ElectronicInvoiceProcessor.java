package processor;

import model.Document;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ElectronicInvoiceProcessor extends AbstractDocumentProcessor {

    @Override
    protected Map<String, String> extractTypeSpecificMetadata(Document document) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("documentNumber", "INV-" + document.getId().substring(0, 8).toUpperCase());
        metadata.put("taxAuthorityReportingStatus", "queued");
        if (document.getTaxId() != null && !document.getTaxId().isBlank()) {
            metadata.put("issuerTaxId", document.getTaxId());
        }
        return metadata;
    }

    @Override
    protected String getEngineName() {
        return "ElectronicInvoiceEngine";
    }

    @Override
    protected String getSuccessMessage(Document document) {
        return "Electronic invoice validated and queued for tax authority reporting in "
                + document.getCountryCode().getDisplayName() + ".";
    }
}

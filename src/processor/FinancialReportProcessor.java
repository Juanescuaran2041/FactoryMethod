package processor;

import model.Document;

import java.util.LinkedHashMap;
import java.util.Map;

public final class FinancialReportProcessor extends AbstractDocumentProcessor {

    @Override
    protected Map<String, String> extractTypeSpecificMetadata(Document document) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("reportReference", "FIN-" + document.getId().substring(0, 8).toUpperCase());
        metadata.put("auditTrailStatus", "recorded");
        return metadata;
    }

    @Override
    protected String getEngineName() {
        return "FinancialReportEngine";
    }

    @Override
    protected String getSuccessMessage(Document document) {
        return "Financial report processed and staged for the audit trail in "
                + document.getCountryCode().getDisplayName() + ".";
    }
}

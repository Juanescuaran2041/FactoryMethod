package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import country.CountryComplianceFactory;
import country.CountryComplianceRegistry;
import country.CountryDocumentRules;
import country.TaxIdValidator;
import model.CountryCode;
import model.DocumentFormat;
import model.DocumentType;

import java.io.IOException;

/**
 * GET /api/metadata - exposes the document types, countries, formats and per-country rules
 * so the HTML client renders its form from the same source of truth the backend validates
 * against, instead of duplicating business rules in JavaScript.
 */
final class MetadataHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                HttpUtils.sendError(exchange, 405, "Only GET is supported on this endpoint.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append('{');

            sb.append("\"documentTypes\":[");
            DocumentType[] types = DocumentType.values();
            for (int i = 0; i < types.length; i++) {
                if (i > 0) sb.append(',');
                sb.append('{');
                JsonWriter.field(sb, "code", types[i].name());
                sb.append(',');
                JsonWriter.field(sb, "label", types[i].getDisplayName());
                sb.append('}');
            }
            sb.append(']');

            sb.append(",\"formats\":[");
            DocumentFormat[] formats = DocumentFormat.values();
            for (int i = 0; i < formats.length; i++) {
                if (i > 0) sb.append(',');
                sb.append(JsonWriter.quote("." + formats[i].name().toLowerCase()));
            }
            sb.append(']');

            sb.append(",\"countries\":[");
            CountryCode[] countries = CountryCode.values();
            CountryComplianceRegistry registry = CountryComplianceRegistry.getInstance();
            for (int i = 0; i < countries.length; i++) {
                if (i > 0) sb.append(',');
                CountryCode country = countries[i];
                CountryComplianceFactory factory = registry.getFactory(country);
                CountryDocumentRules rules = factory.createDocumentRules();
                TaxIdValidator taxIdValidator = factory.createTaxIdValidator();

                sb.append('{');
                JsonWriter.field(sb, "code", country.name());
                sb.append(',');
                JsonWriter.field(sb, "isoCode", country.getIsoCode());
                sb.append(',');
                JsonWriter.field(sb, "label", country.getDisplayName());
                sb.append(',');
                sb.append("\"maxFileSizeBytes\":").append(rules.getMaxFileSizeBytes());
                sb.append(',');
                JsonWriter.field(sb, "taxIdFormat", taxIdValidator.describeExpectedFormat());
                sb.append(',');
                sb.append("\"supportedDocumentTypes\":[");
                boolean first = true;
                for (DocumentType type : DocumentType.values()) {
                    if (rules.isDocumentTypeSupported(type)) {
                        if (!first) sb.append(',');
                        first = false;
                        sb.append(JsonWriter.quote(type.name()));
                    }
                }
                sb.append(']');
                sb.append('}');
            }
            sb.append(']');

            sb.append('}');
            HttpUtils.sendJson(exchange, 200, sb.toString());
        } catch (Exception e) {
            HttpUtils.sendError(exchange, 500, "Unexpected server error: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }
}

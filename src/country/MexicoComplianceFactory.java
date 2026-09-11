package country;

import model.CountryCode;
import model.DocumentType;

import java.util.EnumSet;

/** Mexico accepts every document type. Tax id format: RFC. */
public final class MexicoComplianceFactory extends CountryComplianceFactory {

    @Override
    public CountryCode getCountryCode() {
        return CountryCode.MEXICO;
    }

    @Override
    public TaxIdValidator createTaxIdValidator() {
        return new RegexTaxIdValidator("[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}", "Mexican RFC: 3-4 letters, 6 digits (birth/incorporation date) and a 3-character homoclave (e.g. GODE561231GR8)");
    }

    @Override
    public CountryDocumentRules createDocumentRules() {
        return new StandardCountryDocumentRules(EnumSet.allOf(DocumentType.class), 15L * 1024 * 1024);
    }
}

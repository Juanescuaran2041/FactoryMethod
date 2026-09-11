package country;

import model.CountryCode;
import model.DocumentType;

import java.util.EnumSet;

/** Spain accepts every document type. Tax id format: NIF. */
public final class SpainComplianceFactory extends CountryComplianceFactory {

    @Override
    public CountryCode getCountryCode() {
        return CountryCode.SPAIN;
    }

    @Override
    public TaxIdValidator createTaxIdValidator() {
        return new RegexTaxIdValidator("[0-9]{8}[A-Za-z]", "Spanish NIF: 8 digits followed by a letter (e.g. 12345678Z)");
    }

    @Override
    public CountryDocumentRules createDocumentRules() {
        return new StandardCountryDocumentRules(EnumSet.allOf(DocumentType.class), 12L * 1024 * 1024);
    }
}

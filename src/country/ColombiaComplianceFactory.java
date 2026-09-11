package country;

import model.CountryCode;
import model.DocumentType;

import java.util.EnumSet;

/** Colombia accepts every document type. Tax id format: NIT (9-10 digits, optional check digit). */
public final class ColombiaComplianceFactory extends CountryComplianceFactory {

    @Override
    public CountryCode getCountryCode() {
        return CountryCode.COLOMBIA;
    }

    @Override
    public TaxIdValidator createTaxIdValidator() {
        return new RegexTaxIdValidator("\\d{9,10}(-\\d)?", "Colombian NIT: 9-10 digits, optionally followed by \"-\" and a check digit (e.g. 900123456-7)");
    }

    @Override
    public CountryDocumentRules createDocumentRules() {
        return new StandardCountryDocumentRules(EnumSet.allOf(DocumentType.class), 10L * 1024 * 1024);
    }
}

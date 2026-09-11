package country;

import model.CountryCode;
import model.DocumentType;

import java.util.EnumSet;

/**
 * The United States does not accept Tax Returns through this platform: IRS filings must go
 * through a certified e-file provider, not a general document pipeline. Tax id format: EIN.
 */
public final class UnitedStatesComplianceFactory extends CountryComplianceFactory {

    @Override
    public CountryCode getCountryCode() {
        return CountryCode.UNITED_STATES;
    }

    @Override
    public TaxIdValidator createTaxIdValidator() {
        return new RegexTaxIdValidator("\\d{2}-\\d{7}", "US EIN: 00-0000000");
    }

    @Override
    public CountryDocumentRules createDocumentRules() {
        return new StandardCountryDocumentRules(
                EnumSet.complementOf(EnumSet.of(DocumentType.TAX_RETURN)),
                25L * 1024 * 1024);
    }
}

package country;

import model.CountryCode;
import model.DocumentType;

import java.util.EnumSet;

/**
 * Brazil does not accept Digital Certificates through this platform: locally, digital
 * certificates (ICP-Brasil) must be issued through an accredited certification authority,
 * not uploaded as a document. Tax id format: CNPJ.
 */
public final class BrazilComplianceFactory extends CountryComplianceFactory {

    @Override
    public CountryCode getCountryCode() {
        return CountryCode.BRAZIL;
    }

    @Override
    public TaxIdValidator createTaxIdValidator() {
        return new RegexTaxIdValidator("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", "Brazilian CNPJ: 00.000.000/0000-00");
    }

    @Override
    public CountryDocumentRules createDocumentRules() {
        return new StandardCountryDocumentRules(
                EnumSet.complementOf(EnumSet.of(DocumentType.DIGITAL_CERTIFICATE)),
                20L * 1024 * 1024);
    }
}

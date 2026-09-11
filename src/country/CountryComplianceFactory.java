package country;

import model.CountryCode;

/**
 * Abstract Factory: creates a matching family of country-specific compliance objects
 * (a {@link TaxIdValidator} and a {@link CountryDocumentRules}) that must stay consistent
 * with each other. This pattern is warranted here because GlobalDocs operates in several
 * countries and each one requires its own coherent set of regulatory rules; picking the
 * validator for one country and the rules for another would silently corrupt compliance
 * checks, which is exactly what an Abstract Factory prevents.
 */
public abstract class CountryComplianceFactory {

    public abstract CountryCode getCountryCode();

    public abstract TaxIdValidator createTaxIdValidator();

    public abstract CountryDocumentRules createDocumentRules();
}

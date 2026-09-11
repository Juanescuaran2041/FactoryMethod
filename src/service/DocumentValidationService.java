package service;

import country.CountryComplianceFactory;
import country.CountryComplianceRegistry;
import country.CountryDocumentRules;
import country.TaxIdValidator;
import exception.CountryComplianceException;
import exception.EmptyDocumentException;
import exception.FileTooLargeException;
import exception.UnsupportedCountryException;
import exception.UnsupportedFormatException;
import model.CountryCode;
import model.DocumentFormat;
import model.DocumentType;

/**
 * Validates raw upload data against format, size and per-country compliance rules
 * before a {@link model.Document} is ever constructed. Uses the {@link CountryComplianceRegistry}
 * Singleton to fetch the {@link CountryComplianceFactory} (Abstract Factory) for the target country.
 */
public final class DocumentValidationService {

    public DocumentFormat validateFormat(String fileName) throws UnsupportedFormatException {
        return DocumentFormat.fromFileName(fileName);
    }

    public void validateNotEmpty(byte[] content, String fileName) throws EmptyDocumentException {
        if (content == null || content.length == 0) {
            throw new EmptyDocumentException("File \"" + fileName + "\" is empty (0 bytes).");
        }
    }

    public CountryDocumentRules validateCountryAcceptsDocumentType(CountryCode countryCode, DocumentType documentType)
            throws UnsupportedCountryException, CountryComplianceException {
        CountryComplianceFactory complianceFactory = CountryComplianceRegistry.getInstance().getFactory(countryCode);
        CountryDocumentRules rules = complianceFactory.createDocumentRules();
        if (!rules.isDocumentTypeSupported(documentType)) {
            throw new CountryComplianceException(
                    countryCode.getDisplayName() + " does not accept \"" + documentType.getDisplayName()
                            + "\" through this platform. Accepted types: " + rules.describeSupportedDocumentTypes() + ".");
        }
        return rules;
    }

    public void validateSize(byte[] content, CountryDocumentRules rules, String fileName) throws FileTooLargeException {
        if (content.length > rules.getMaxFileSizeBytes()) {
            throw new FileTooLargeException(
                    "File \"" + fileName + "\" is " + content.length + " bytes, which exceeds the "
                            + rules.getMaxFileSizeBytes() + " byte limit for this country.");
        }
    }

    public void validateTaxIdIfProvided(CountryCode countryCode, String taxId) throws CountryComplianceException, UnsupportedCountryException {
        if (taxId == null || taxId.isBlank()) {
            return;
        }
        TaxIdValidator validator = CountryComplianceRegistry.getInstance().getFactory(countryCode).createTaxIdValidator();
        if (!validator.isValid(taxId)) {
            throw new CountryComplianceException(
                    "\"" + taxId + "\" is not a valid tax id for " + countryCode.getDisplayName()
                            + ". Expected format: " + validator.describeExpectedFormat() + ".");
        }
    }
}

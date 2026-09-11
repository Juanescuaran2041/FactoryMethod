package service;

import country.CountryDocumentRules;
import exception.DocumentProcessingException;
import exception.UnsupportedCountryException;
import exception.UnsupportedDocumentTypeException;
import factory.DocumentProcessorFactory;
import factory.DocumentProcessorFactoryProvider;
import model.CountryCode;
import model.Document;
import model.DocumentFormat;
import model.DocumentType;
import model.ProcessingResult;

/**
 * Facade used by both the single-document and batch API endpoints. Runs the full real
 * pipeline (parse request fields -> validate format/size/country compliance -> build
 * {@link Document} -> resolve the {@link DocumentProcessorFactory} via the Factory Method
 * Singleton provider -> process) and NEVER throws: every failure is converted into a
 * {@link ProcessingResult} with status REJECTED (validation failure) or FAILED (unexpected
 * error), so one bad document can never abort a batch.
 */
public final class DocumentProcessingService {

    private final DocumentValidationService validationService = new DocumentValidationService();

    public ProcessingResult processUpload(String fileName,
                                           byte[] content,
                                           String documentTypeRaw,
                                           String countryCodeRaw,
                                           String taxId) {
        String displayDocumentType = documentTypeRaw;
        String displayCountry = countryCodeRaw;
        try {
            DocumentType documentType = parseDocumentType(documentTypeRaw);
            CountryCode countryCode = parseCountryCode(countryCodeRaw);
            displayDocumentType = documentType.getDisplayName();
            displayCountry = countryCode.getDisplayName();

            validationService.validateNotEmpty(content, fileName);
            DocumentFormat format = validationService.validateFormat(fileName);
            CountryDocumentRules rules = validationService.validateCountryAcceptsDocumentType(countryCode, documentType);
            validationService.validateSize(content, rules, fileName);
            validationService.validateTaxIdIfProvided(countryCode, taxId);

            Document document = new Document(fileName, documentType, format, countryCode, content, taxId);

            DocumentProcessorFactory factory = DocumentProcessorFactoryProvider.getInstance().getFactory(documentType);
            return factory.handle(document);
        } catch (DocumentProcessingException e) {
            return ProcessingResult.rejected(fileName, displayDocumentType, displayCountry, e.getMessage());
        } catch (RuntimeException e) {
            return ProcessingResult.failed(fileName, displayDocumentType, displayCountry,
                    "Unexpected error while processing this document: " + e.getMessage());
        }
    }

    private DocumentType parseDocumentType(String raw) throws UnsupportedDocumentTypeException {
        try {
            return DocumentType.fromCode(raw);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedDocumentTypeException(e.getMessage());
        }
    }

    private CountryCode parseCountryCode(String raw) throws UnsupportedCountryException {
        try {
            return CountryCode.fromCode(raw);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedCountryException(e.getMessage());
        }
    }
}

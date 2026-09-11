package factory;

import exception.UnsupportedDocumentTypeException;
import model.DocumentType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Singleton: single, application-wide access point that resolves the correct
 * {@link DocumentProcessorFactory} for a given {@link DocumentType}. Centralizing this
 * lookup avoids scattering {@code new XxxProcessorFactory()} calls (and the switch/if-chain
 * that would come with them) across the service and web layers.
 */
public final class DocumentProcessorFactoryProvider {

    private static final DocumentProcessorFactoryProvider INSTANCE = new DocumentProcessorFactoryProvider();

    private final Map<DocumentType, DocumentProcessorFactory> factories = new EnumMap<>(DocumentType.class);

    private DocumentProcessorFactoryProvider() {
        register(new ElectronicInvoiceProcessorFactory());
        register(new LegalContractProcessorFactory());
        register(new FinancialReportProcessorFactory());
        register(new DigitalCertificateProcessorFactory());
        register(new TaxReturnProcessorFactory());
    }

    public static DocumentProcessorFactoryProvider getInstance() {
        return INSTANCE;
    }

    private void register(DocumentProcessorFactory factory) {
        factories.put(factory.getSupportedType(), factory);
    }

    public DocumentProcessorFactory getFactory(DocumentType documentType) throws UnsupportedDocumentTypeException {
        DocumentProcessorFactory factory = factories.get(documentType);
        if (factory == null) {
            throw new UnsupportedDocumentTypeException("No processor is registered for document type " + documentType + ".");
        }
        return factory;
    }
}

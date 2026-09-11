package factory;

import model.DocumentType;
import processor.DocumentProcessor;
import processor.ElectronicInvoiceProcessor;

public final class ElectronicInvoiceProcessorFactory extends DocumentProcessorFactory {

    @Override
    protected DocumentProcessor createProcessor() {
        return new ElectronicInvoiceProcessor();
    }

    @Override
    public DocumentType getSupportedType() {
        return DocumentType.ELECTRONIC_INVOICE;
    }
}

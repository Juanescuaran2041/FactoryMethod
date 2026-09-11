package factory;

import model.DocumentType;
import processor.DocumentProcessor;
import processor.TaxReturnProcessor;

public final class TaxReturnProcessorFactory extends DocumentProcessorFactory {

    @Override
    protected DocumentProcessor createProcessor() {
        return new TaxReturnProcessor();
    }

    @Override
    public DocumentType getSupportedType() {
        return DocumentType.TAX_RETURN;
    }
}

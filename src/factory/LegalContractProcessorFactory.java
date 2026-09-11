package factory;

import model.DocumentType;
import processor.DocumentProcessor;
import processor.LegalContractProcessor;

public final class LegalContractProcessorFactory extends DocumentProcessorFactory {

    @Override
    protected DocumentProcessor createProcessor() {
        return new LegalContractProcessor();
    }

    @Override
    public DocumentType getSupportedType() {
        return DocumentType.LEGAL_CONTRACT;
    }
}

package factory;

import model.DocumentType;
import processor.DigitalCertificateProcessor;
import processor.DocumentProcessor;

public final class DigitalCertificateProcessorFactory extends DocumentProcessorFactory {

    @Override
    protected DocumentProcessor createProcessor() {
        return new DigitalCertificateProcessor();
    }

    @Override
    public DocumentType getSupportedType() {
        return DocumentType.DIGITAL_CERTIFICATE;
    }
}

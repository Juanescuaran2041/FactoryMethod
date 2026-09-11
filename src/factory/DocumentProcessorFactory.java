package factory;

import exception.DocumentProcessingException;
import model.Document;
import model.DocumentType;
import model.ProcessingResult;
import processor.DocumentProcessor;

/** Factory Method **/

public abstract class DocumentProcessorFactory {

    public final ProcessingResult handle(Document document) throws DocumentProcessingException {
        DocumentProcessor processor = createProcessor();
        return processor.process(document);
    }

    /** The Factory Method. */
    protected abstract DocumentProcessor createProcessor();

    public abstract DocumentType getSupportedType();
}

package processor;

import exception.DocumentProcessingException;
import model.Document;
import model.ProcessingResult;

/**
 * Product interface of the Factory Method implemented in the {@code factory} package.
 * Every document type has its own concrete processor.
 */
public interface DocumentProcessor {

    ProcessingResult process(Document document) throws DocumentProcessingException;
}

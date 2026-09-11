package factory;

import model.DocumentType;
import processor.DocumentProcessor;
import processor.FinancialReportProcessor;

public final class FinancialReportProcessorFactory extends DocumentProcessorFactory {

    @Override
    protected DocumentProcessor createProcessor() {
        return new FinancialReportProcessor();
    }

    @Override
    public DocumentType getSupportedType() {
        return DocumentType.FINANCIAL_REPORT;
    }
}

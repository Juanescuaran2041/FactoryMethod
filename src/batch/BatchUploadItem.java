package batch;

/** One file within a batch upload request, paired with the document type requested for it. */
public final class BatchUploadItem {

    private final String fileName;
    private final byte[] content;
    private final String documentTypeRaw;

    public BatchUploadItem(String fileName, byte[] content, String documentTypeRaw) {
        this.fileName = fileName;
        this.content = content;
        this.documentTypeRaw = documentTypeRaw;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public String getDocumentTypeRaw() {
        return documentTypeRaw;
    }
}

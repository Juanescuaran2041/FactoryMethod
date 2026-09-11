package exception;

/**
 * Thrown when an uploaded file's extension is not one of the formats GlobalDocs supports
 * (for example a video, audio or image file).
 */
public class UnsupportedFormatException extends DocumentProcessingException {
    public UnsupportedFormatException(String message) {
        super(message);
    }
}

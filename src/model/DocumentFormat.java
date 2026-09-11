package model;

import exception.UnsupportedFormatException;

import java.util.Locale;

/**
 * File formats accepted by the platform. Any other extension (video, audio, images, etc.)
 * must be rejected by {@link #fromFileName(String)} before a document ever reaches a processor.
 */
public enum DocumentFormat {
    PDF,
    DOC,
    DOCX,
    XLSX,
    MD,
    CSV,
    TXT;

    public static DocumentFormat fromFileName(String fileName) throws UnsupportedFormatException {
        if (fileName == null || fileName.isBlank()) {
            throw new UnsupportedFormatException("The uploaded file has no name.");
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            throw new UnsupportedFormatException(
                    "File \"" + fileName + "\" has no file extension, so its format cannot be determined.");
        }
        String extension = fileName.substring(dotIndex + 1).toUpperCase(Locale.ROOT);
        try {
            return DocumentFormat.valueOf(extension);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedFormatException(
                    "Format \"." + extension.toLowerCase(Locale.ROOT) + "\" is not supported. Allowed formats are: "
                            + allowedFormatsDescription() + ".");
        }
    }

    public static String allowedFormatsDescription() {
        StringBuilder builder = new StringBuilder();
        DocumentFormat[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(".").append(values[i].name().toLowerCase(Locale.ROOT));
        }
        return builder.toString();
    }
}

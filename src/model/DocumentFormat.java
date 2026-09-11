package model;

import java.util.Locale;

public enum DocumentFormat {
    PDF,
    DOC,
    DOCX,
    XLSX,
    MD,
    CSV,
    TXT;

    public static DocumentFormat fromExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("File does not have a valid extension" + fileName);
        }
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase(Locale.ROOT);
        try {
            return DocumentFormat.valueOf(ext);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Format not allowed: ." + ext.toLowerCase(Locale.ROOT));
        }
    }

}

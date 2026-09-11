package web;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal, dependency-free parser for {@code multipart/form-data} request bodies, so the
 * HTML client can upload real files directly to the plain {@code com.sun.net.httpserver}
 * backend without pulling in a servlet container or an external multipart library.
 */
public final class MultipartParser {

    private static final Pattern NAME_PATTERN = Pattern.compile("name=\"([^\"]*)\"");
    private static final Pattern FILENAME_PATTERN = Pattern.compile("filename=\"([^\"]*)\"");

    private MultipartParser() {
    }

    public static List<Part> parse(byte[] body, String boundary) {
        byte[] delimiter = ("--" + boundary).getBytes(StandardCharsets.ISO_8859_1);
        List<Integer> markers = findAll(body, delimiter);
        List<Part> parts = new ArrayList<>();

        for (int i = 0; i < markers.size() - 1; i++) {
            int chunkStart = markers.get(i) + delimiter.length;
            int chunkEnd = markers.get(i + 1);
            if (chunkStart >= chunkEnd) {
                continue;
            }
            chunkStart = skipLeadingCrLf(body, chunkStart, chunkEnd);
            chunkEnd = trimTrailingCrLf(body, chunkStart, chunkEnd);

            int headerEnd = indexOf(body, "\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1), chunkStart, chunkEnd);
            if (headerEnd < 0) {
                continue;
            }
            String headerText = new String(body, chunkStart, headerEnd - chunkStart, StandardCharsets.UTF_8);
            int dataStart = headerEnd + 4;
            byte[] data = java.util.Arrays.copyOfRange(body, dataStart, chunkEnd);

            String name = extract(NAME_PATTERN, headerText);
            String filename = extract(FILENAME_PATTERN, headerText);
            String contentType = extractContentType(headerText);
            parts.add(new Part(name, filename, contentType, data));
        }
        return parts;
    }

    private static int skipLeadingCrLf(byte[] body, int start, int end) {
        if (start + 1 < end && body[start] == '\r' && body[start + 1] == '\n') {
            return start + 2;
        }
        return start;
    }

    private static int trimTrailingCrLf(byte[] body, int start, int end) {
        if (end - 2 >= start && body[end - 2] == '\r' && body[end - 1] == '\n') {
            return end - 2;
        }
        return end;
    }

    private static String extract(Pattern pattern, String headerText) {
        Matcher matcher = pattern.matcher(headerText);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static String extractContentType(String headerText) {
        for (String line : headerText.split("\r\n")) {
            if (line.toLowerCase().startsWith("content-type:")) {
                return line.substring(line.indexOf(':') + 1).trim();
            }
        }
        return null;
    }

    private static List<Integer> findAll(byte[] haystack, byte[] needle) {
        List<Integer> positions = new ArrayList<>();
        int from = 0;
        int found;
        while ((found = indexOf(haystack, needle, from, haystack.length)) >= 0) {
            positions.add(found);
            from = found + needle.length;
        }
        return positions;
    }

    private static int indexOf(byte[] haystack, byte[] needle, int from, int to) {
        outer:
        for (int i = from; i <= to - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i + j] != needle[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    public static String extractBoundary(String contentTypeHeader) {
        if (contentTypeHeader == null) {
            return null;
        }
        for (String segment : contentTypeHeader.split(";")) {
            String trimmed = segment.trim();
            if (trimmed.startsWith("boundary=")) {
                String value = trimmed.substring("boundary=".length()).trim();
                if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                    value = value.substring(1, value.length() - 1);
                }
                return value;
            }
        }
        return null;
    }

    public static final class Part {
        public final String name;
        public final String filename;
        public final String contentType;
        public final byte[] data;

        Part(String name, String filename, String contentType, byte[] data) {
            this.name = name;
            this.filename = filename;
            this.contentType = contentType;
            this.data = data;
        }

        public boolean isFile() {
            return filename != null && !filename.isBlank();
        }

        public String asText() {
            return new String(data, StandardCharsets.UTF_8).trim();
        }
    }
}

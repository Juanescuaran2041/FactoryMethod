package web;

import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** Small helpers shared by every HTTP handler in this package. */
final class HttpUtils {

    private HttpUtils() {
    }

    static byte[] readFully(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int read;
        while ((read = input.read(chunk)) != -1) {
            buffer.write(chunk, 0, read);
        }
        return buffer.toByteArray();
    }

    static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] payload = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, payload.length);
        try (OutputStream body = exchange.getResponseBody()) {
            body.write(payload);
        }
    }

    static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        JsonWriter.field(sb, "error", message);
        sb.append('}');
        sendJson(exchange, statusCode, sb.toString());
    }
}

package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Serves the small, fixed set of static files that make up the HTML client. Routes are
 * whitelisted explicitly (no generic path-based file serving) to avoid any path traversal risk.
 */
final class StaticFileHandler implements HttpHandler {

    private static final Map<String, String> ROUTES = Map.of(
            "/", "index.html",
            "/index.html", "index.html",
            "/styles.css", "styles.css",
            "/app.js", "app.js"
    );

    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "html", "text/html; charset=utf-8",
            "css", "text/css; charset=utf-8",
            "js", "application/javascript; charset=utf-8"
    );

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                HttpUtils.sendError(exchange, 405, "Only GET is supported on this endpoint.");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String resourceName = ROUTES.get(path);
            if (resourceName == null) {
                HttpUtils.sendError(exchange, 404, "Not found: " + path);
                return;
            }

            byte[] content = loadResource(resourceName);
            if (content == null) {
                HttpUtils.sendError(exchange, 500, "Static resource \"" + resourceName + "\" is missing from the server.");
                return;
            }

            String extension = resourceName.substring(resourceName.lastIndexOf('.') + 1);
            String contentType = CONTENT_TYPES.getOrDefault(extension, "application/octet-stream");
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream body = exchange.getResponseBody()) {
                body.write(content);
            }
        } finally {
            exchange.close();
        }
    }

    private byte[] loadResource(String resourceName) throws IOException {
        try (InputStream classpathStream = getClass().getClassLoader().getResourceAsStream("webapp/" + resourceName)) {
            if (classpathStream != null) {
                return HttpUtils.readFully(classpathStream);
            }
        }
        for (String candidate : new String[]{"src/webapp/" + resourceName, "webapp/" + resourceName}) {
            File file = new File(candidate);
            if (file.isFile()) {
                try (InputStream fileStream = new FileInputStream(file)) {
                    return HttpUtils.readFully(fileStream);
                }
            }
        }
        return null;
    }
}

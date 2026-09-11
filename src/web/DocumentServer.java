package web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Entry point of the GlobalDocs Special Document Processing System. Starts a plain JDK HTTP
 * server (no external framework) that serves the HTML client and the JSON API it talks to
 * directly, so the whole request/response flow (upload -> validate -> process -> respond)
 * runs against the real backend described in the assignment.
 */
public final class DocumentServer {

    private static final int DEFAULT_PORT = 8080;

    private DocumentServer() {
    }

    public static void main(String[] args) throws IOException {
        int port = resolvePort(args);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/metadata", new MetadataHandler());
        server.createContext("/api/documents/process", new ProcessDocumentHandler());
        server.createContext("/api/documents/batch", new BatchUploadHandler());
        server.createContext("/", new StaticFileHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("GlobalDocs Document Processing System listening on http://localhost:" + port);
    }

    private static int resolvePort(String[] args) {
        if (args.length > 0) {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                // fall through to default port
            }
        }
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            try {
                return Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {
                // fall through to default port
            }
        }
        return DEFAULT_PORT;
    }
}

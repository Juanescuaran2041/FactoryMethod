import web.DocumentServer;

/**
 * Convenience entry point so the application can be started as {@code java Main}
 * in addition to {@code java web.DocumentServer}.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        DocumentServer.main(args);
    }
}
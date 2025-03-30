package org.learn.core;

import org.learn.http.HttpParser;
import org.learn.http.HttpResponse;
import org.learn.http.HttpRequest;
import org.learn.http.StatusCode;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles client connections and processes HTTP requests
 */
public class ConnectionHandler implements Runnable {
    private final Socket clientSocket;
    private static final String WEB_ROOT = "webroot"; // Directory for serving static files
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        // Initialize common MIME types
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("json", "application/json");
        MIME_TYPES.put("xml", "application/xml");
        MIME_TYPES.put("pdf", "application/pdf");
    }

    public ConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            // Parse the HTTP request
            HttpRequest request = HttpParser.parse(clientSocket.getInputStream());
            System.out.println("Received request: " + request);

            // Process the request and generate response
            HttpResponse response = handleRequest(request);

            // Send the response
            clientSocket.getOutputStream().write(response.build());

            // Close the connection
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
            try {
                clientSocket.close();
            } catch (IOException ex) {
                // Ignore
            }
        }
    }

    /**
     * Process the request and generate an appropriate response
     */
    private HttpResponse handleRequest(HttpRequest request) {
        switch (request.getMethod()) {
            case GET:
                return handleGetRequest(request);
            case HEAD:
                // HEAD is like GET but without body
                HttpResponse response = handleGetRequest(request);
                response.setBody(new byte[0]);
                return response;
            case OPTIONS:
                return handleOptionsRequest(request);
            case POST:
            case PUT:
            case DELETE:
            case PATCH:
                // Not implemented in this basic version
                return HttpResponse.makeStatusResponse(
                        StatusCode.NOT_IMPLEMENTED,
                        "Method " + request.getMethod() + " is not implemented yet."
                );
            default:
                return HttpResponse.makeStatusResponse(
                        StatusCode.BAD_REQUEST,
                        "Unknown HTTP method: " + request.getMethod()
                );
        }
    }

    /**
     * Handle GET requests - primarily for serving static files
     */
    private HttpResponse handleGetRequest(HttpRequest request) {
        String path = request.getPath();

        // Handle root path
        if ("/".equals(path)) {
            path = "/index.html";
        }

        // Prevent directory traversal attacks
        if (path.contains("..")) {
            return HttpResponse.makeStatusResponse(
                    StatusCode.FORBIDDEN,
                    "Path traversal is not allowed."
            );
        }

        // Check if the file exists
        Path filePath = Paths.get(WEB_ROOT, path.substring(1));
        File file = filePath.toFile();

        if (!file.exists() || file.isDirectory()) {
            return HttpResponse.notFound();
        }

        // Try to serve the file
        try {
            byte[] fileContent = Files.readAllBytes(filePath);

            // Determine content type based on file extension
            String contentType = getContentType(filePath.toString());

            return new HttpResponse()
                    .setStatusCode(StatusCode.OK)
                    .addHeader("Content-Type", contentType)
                    .setBody(fileContent);
        } catch (IOException e) {
            return HttpResponse.internalServerError("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Handle OPTIONS requests - returns allowed methods
     */
    private HttpResponse handleOptionsRequest(HttpRequest request) {
        return new HttpResponse()
                .setStatusCode(StatusCode.OK)
                .addHeader("Allow", "GET, HEAD, OPTIONS")
                .setBody("");
    }

    /**
     * Get content type based on file extension
     */
    private String getContentType(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot >= 0) {
            String extension = path.substring(lastDot + 1).toLowerCase();
            String contentType = MIME_TYPES.get(extension);
            if (contentType != null) {
                return contentType;
            }
        }
        // Default content type
        return "application/octet-stream";
    }
}
package org.learn.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpParser {
    public static HttpRequest parse(InputStream inputStream) throws IOException {
        HttpRequest request = new HttpRequest();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IOException("Empty request line");
        }

        parseRequestLine(request, requestLine);

        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isBlank()) {
            parseHeaderLine(request, headerLine);
        }

        if (request.getHeader("Content-Length") != null) {
            int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
            if (contentLength > 0) {
                char[] body = new char[contentLength];
                reader.read(body, 0, contentLength);
                request.setBody(new String(body));
            }
        }

        return request;
    }

    private static void parseRequestLine(HttpRequest request, String requestLine) {
        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid request line: " + requestLine);
        }

        try {
            request.setMethod(HttpMethod.valueOf(parts[0]));
        } catch (IllegalArgumentException e) {
            request.setMethod(HttpMethod.UNKNOWN);
        }

        String pathAndQuery = parts[1];
        if (pathAndQuery.contains("?")) {
            String[] pathParts = pathAndQuery.split("\\?", 2);
            request.setPath(pathParts[0]);
            parseQueryParameters(request, pathParts[1]);
        } else {
            request.setPath(pathAndQuery);
        }

        request.setVersion(parts[2]);
    }

    private static void parseHeaderLine(HttpRequest request, String headerLine) {
        int colonPos = headerLine.indexOf(':');
        if (colonPos > 0) {
            String name = headerLine.substring(0, colonPos).trim();
            String value = headerLine.substring(colonPos + 1).trim();
            request.addHeader(name, value);
        }
    }

    private static void parseQueryParameters(HttpRequest request, String queryString) {
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int equalPos = pair.indexOf('=');
            if (equalPos > 0) {
                String name = pair.substring(0, equalPos);
                String value = pair.substring(equalPos + 1);
                request.addQueryParameter(name, value);
            } else {
                request.addQueryParameter(pair, "");
            }
        }
    }
}

package org.learn.http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private StatusCode statusCode;
    private Map<String, String> headers;
    private byte[] body;
    private static final String CRLF = "\r\n";

    public HttpResponse() {
        this.statusCode = StatusCode.OK;
        this.headers = new HashMap<>();
        this.body = new byte[0];

        this.addHeader("Server", "SimpleJavaHttpServer/1.0");
        this.addHeader("Connection", "close");
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public HttpResponse setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse addHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public HttpResponse setBody(byte[] body) {
        this.body = body;
        this.addHeader("Content-Length", String.valueOf(body.length));
        return this;
    }

    public HttpResponse setBody(String body) {
        return setBody(body.getBytes(StandardCharsets.UTF_8));
    }

    public HttpResponse html(String content) {
        this.addHeader("Content-Type", "text/html; charset=utf-8");
        return setBody(content);
    }

    public HttpResponse text(String content) {
        this.addHeader("Content-Type", "text/plain; charset=utf-8");
        return setBody(content);
    }

    public HttpResponse json(String content) {
        this.addHeader("Content-Type", "application/json; charset=utf-8");
        return setBody(content);
    }

    public byte[] build() {
        StringBuilder responseBuilder = new StringBuilder();

        responseBuilder.append("HTTP/1.1 ")
                       .append(statusCode.getCode())
                       .append(" ")
                       .append(statusCode.getMessage())
                       .append(CRLF);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseBuilder.append(header.getKey())
                           .append(": ")
                           .append(header.getValue())
                           .append(CRLF);
        }

        responseBuilder.append(CRLF);

        byte[] headerBytes = responseBuilder.toString().getBytes(StandardCharsets.UTF_8);

        byte[] response = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
        System.arraycopy(body, 0, response, headerBytes.length, body.length);

        return response;
    }

    public static HttpResponse makeStatusResponse(StatusCode statusCode, String message) {
        String htmlBody = "<!DOCTYPE html>\n" +
                          "<html>\n" +
                          "<head><title>" + statusCode.getCode() + " " + statusCode.getMessage() + "</title></head>\n" +
                          "<body>\n" +
                          "<h1>" + statusCode.getCode() + " " + statusCode.getMessage() + "</h1>\n" +
                          "<p>" + message + "</p>\n" +
                          "</body>\n>" +
                          "</html>";

        return new HttpResponse()
                .setStatusCode(statusCode)
                .html(htmlBody);
    }

//    Create a 404 Not Found response
    public static HttpResponse notFound() {
        return makeStatusResponse(StatusCode.NOT_FOUND, "The requested resource is not found.");
    }

//    Create a 500 Internal Server Error response
    public static HttpResponse internalServerError(String message) {
        return makeStatusResponse(StatusCode.INTERNAL_SERVER_ERROR, message);
    }
}

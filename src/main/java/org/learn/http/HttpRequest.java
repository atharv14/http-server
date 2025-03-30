package org.learn.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private HttpMethod method;
    private String path;
    private String version;
    private Map<String, String> headers;
    private String body;
    private Map<String, String> queryParameters;

    public HttpRequest() {
        this.headers = new HashMap<>();
        this.queryParameters = new HashMap<>();
    }

    // Getters and setters
    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String name, String value) {
        this.headers.put(name.toLowerCase(), value);
    }

    public String getHeader(String name) {
        return this.headers.get(name.toLowerCase());
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public void addQueryParameter(String name, String value) {
        this.queryParameters.put(name, value);
    }

    public String toString() {
        return method + " " + path + " " + version;
    }
}

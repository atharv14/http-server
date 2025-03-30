package org.learn.http;

public enum StatusCode {
    OK(200, "OK"),
    CREATED(201, "CREATED"),
    ACCEPTED(202, "ACCEPTED"),
    NO_CONTENT(204, "NO CONTENT"),
    MOVED_PERMANENTLY(301, "MOVED PERMANENTLY"),
    FOUND(302, "FOUND"),
    SEE_OTHER(303, "SEE OTHER"),
    NOT_MODIFIED(304, "NOT MODIFIED"),
    BAD_REQUEST(400, "BAD REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),
    NOT_FOUND(404, "NOT FOUND"),
    METHOD_NOT_ALLOWED(405, "METHOD NOT ALLOWED"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR"),
    NOT_IMPLEMENTED(501, "NOT IMPLEMENTED"),
    BAD_GATEWAY(502, "BAD GATEWAY"),
    SERVICE_UNAVAILABLE(503, "SERVICE UNAVAILABLE");

    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

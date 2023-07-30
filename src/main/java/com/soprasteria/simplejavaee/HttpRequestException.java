package com.soprasteria.simplejavaee;

public class HttpRequestException extends RuntimeException {
    private final int statusCode;

    public HttpRequestException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

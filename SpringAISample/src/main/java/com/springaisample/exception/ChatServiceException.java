package com.springaisample.exception;

/**
 * Base exception for all chat service related errors
 */
public class ChatServiceException extends RuntimeException {
    private final int statusCode;

    public ChatServiceException(String message) {
        super(message);
        this.statusCode = 500;
    }

    public ChatServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
    }

    public ChatServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

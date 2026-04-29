package com.springaisample.exception;

/**
 * Exception thrown when Ollama model backend is unavailable
 */
public class ModelUnavailableException extends ChatServiceException {
    public ModelUnavailableException(String message) {
        super(message, 503);
    }

    public ModelUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

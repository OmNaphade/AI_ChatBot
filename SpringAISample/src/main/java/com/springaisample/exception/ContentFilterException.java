package com.springaisample.exception;

/**
 * Exception thrown when content triggers safety guardrails
 */
public class ContentFilterException extends ChatServiceException {
    public ContentFilterException(String message) {
        super(message, 400);
    }
}

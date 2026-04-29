package com.springaisample.exception;

/**
 * Exception thrown when session is not found or expired
 */
public class SessionNotFoundException extends ChatServiceException {
    public SessionNotFoundException(String sessionId) {
        super("Session not found: " + sessionId, 404);
    }
}


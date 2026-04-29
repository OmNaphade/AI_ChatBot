package com.springaisample.exception;

/**
 * Exception thrown when rate limit is exceeded
 */
public class RateLimitExceededException extends ChatServiceException {
    public RateLimitExceededException(String message) {
        super(message, 429);
    }
}

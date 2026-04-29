package com.springaisample.exception;

/**
 * Exception thrown when conversation context exceeds model token limit
 */
public class ContextWindowExceededException extends ChatServiceException {
    private final int maxTokens;
    private final int actualTokens;

    public ContextWindowExceededException(int max, int actual) {
        super(String.format("Context exceeded: %d tokens used, max is %d", actual, max), 400);
        this.maxTokens = max;
        this.actualTokens = actual;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public int getActualTokens() {
        return actualTokens;
    }
}

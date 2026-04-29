package com.springaisample.exception;

/**
 * Exception thrown when training data ingestion fails
 */
public class DataIngestionException extends TrainingPipelineException {
    public DataIngestionException(String message) {
        super(message);
    }

    public DataIngestionException(String message, Throwable cause) {
        super(message, cause);
    }
}


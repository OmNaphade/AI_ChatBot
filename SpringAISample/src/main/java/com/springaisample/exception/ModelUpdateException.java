package com.springaisample.exception;

/**
 * Exception thrown when model update fails
 */
public class ModelUpdateException extends TrainingPipelineException {
    public ModelUpdateException(String message) {
        super(message);
    }

    public ModelUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

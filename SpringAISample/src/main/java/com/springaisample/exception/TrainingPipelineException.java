package com.springaisample.exception;

/**
 * Base exception for training pipeline failures
 */
public class TrainingPipelineException extends ChatServiceException {
    public TrainingPipelineException(String message) {
        super(message, 500);
    }

    public TrainingPipelineException(String message, Throwable cause) {
        super(message, cause);
    }
}

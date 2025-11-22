package com.example.device.api.exception;

/**
 * Thrown when an operation is not allowed.
 */
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}

package com.example.device.api.exception.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enumeration describing API error codes with associated HTTP status and default messages.
 */
@Getter
public enum ErrorCode {

    DEVICE_NOT_FOUND("DEVICE_NOT_FOUND", HttpStatus.NOT_FOUND, "Device not found"),
    DEVICE_ALREADY_EXISTS("DEVICE_ALREADY_EXISTS", HttpStatus.CONFLICT, "Device already exists"),
    INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST, "Invalid request data"),
    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error"),
    FORBIDDEN_OPERATION("FORBIDDEN_OPERATION", HttpStatus.FORBIDDEN, "Operation is not allowed");

    private final String code;
    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(String code, HttpStatus status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

}

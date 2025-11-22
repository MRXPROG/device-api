package com.example.device.api.exception.dto;

import com.example.device.api.exception.constants.ErrorCode;
import com.example.device.api.exception.constants.type.ErrorType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Unified API error response DTO.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetailsDto {

    private final LocalDateTime timestamp;
    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
    private final ErrorType errorType;

    public ErrorDetailsDto(ErrorCode errorCode, String message) {
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode.getCode();
        this.errorMessage = message != null ? message : errorCode.getDefaultMessage();
        this.errorType = mapType(errorCode);
        this.httpStatus = errorCode.getStatus();
    }

    private ErrorType mapType(ErrorCode error) {
        return switch (error) {
            case DEVICE_NOT_FOUND -> ErrorType.NOT_FOUND;
            case DEVICE_ALREADY_EXISTS -> ErrorType.CONFLICT;
            case INVALID_REQUEST -> ErrorType.VALIDATION_ERROR;
            default -> ErrorType.INTERNAL_ERROR;
        };
    }

    public static ErrorDetailsDto of(ErrorCode code, String msg) {
        return new ErrorDetailsDto(code, msg);
    }
}

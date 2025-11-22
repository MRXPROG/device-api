package com.example.device.api.exception;

import com.example.device.api.exception.constants.ErrorCode;
import com.example.device.api.exception.constants.type.ErrorType;
import com.example.device.api.exception.dto.ErrorDetailsDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorDetailsDtoTest {

    @Test
    void of_ShouldSetAllFieldsCorrectly() {
        ErrorDetailsDto dto = ErrorDetailsDto.of(ErrorCode.INVALID_REQUEST, "Bad format");

        assertNotNull(dto.getTimestamp());
        assertEquals("INVALID_REQUEST", dto.getErrorCode());
        assertEquals("Bad format", dto.getErrorMessage());
        assertEquals(ErrorType.VALIDATION_ERROR, dto.getErrorType());
        assertEquals(HttpStatus.BAD_REQUEST, dto.getHttpStatus());
    }

    @Test
    void constructor_ShouldUseDefaultMessage_WhenMessageIsNull() {
        ErrorDetailsDto dto = new ErrorDetailsDto(ErrorCode.DEVICE_NOT_FOUND, null);

        assertEquals(ErrorCode.DEVICE_NOT_FOUND.getDefaultMessage(), dto.getErrorMessage());
        assertEquals(ErrorType.NOT_FOUND, dto.getErrorType());
        assertEquals(HttpStatus.NOT_FOUND, dto.getHttpStatus());
    }

    @Test
    void mapType_ShouldReturnConflict_ForAlreadyExists() {
        ErrorDetailsDto dto = new ErrorDetailsDto(ErrorCode.DEVICE_ALREADY_EXISTS, "exists");

        assertEquals(ErrorType.CONFLICT, dto.getErrorType());
    }

    @Test
    void mapType_ShouldReturnValidationError_ForInvalidRequest() {
        ErrorDetailsDto dto = new ErrorDetailsDto(ErrorCode.INVALID_REQUEST, "invalid");

        assertEquals(ErrorType.VALIDATION_ERROR, dto.getErrorType());
    }

    @Test
    void mapType_ShouldReturnInternalError_ForInternalError() {
        ErrorDetailsDto dto = new ErrorDetailsDto(ErrorCode.INTERNAL_ERROR, "error");

        assertEquals(ErrorType.INTERNAL_ERROR, dto.getErrorType());
    }
}

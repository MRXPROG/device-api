package com.example.device.api.exception;

import com.example.device.api.exception.handler.GlobalExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/device-api/devices");
    }

    @Test
    void handleThrowable_Returns500() {
        Throwable ex = new RuntimeException("Boom!");

        var response = handler.handleThrowable(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Boom!", response.getBody().getErrorMessage());
    }

    @Test
    void handleConstraintViolation_Returns400() {
        var ex = new jakarta.validation.ConstraintViolationException("Invalid field", null);

        var response = handler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid field", response.getBody().getErrorMessage());
    }

    @Test
    void handleAlreadyExists_Returns409() {
        var ex = new DeviceAlreadyExistsException("iPhone", "Apple");

        var response = handler.handleAlreadyExists(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().getErrorMessage().contains("already exists"));
    }

    @Test
    void handleNotFound_Returns404() {
        var ex = new DeviceNotFoundException(123L);

        var response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getErrorMessage().contains("123"));
    }

    @Test
    void handleForbidden_Returns403() {
        var ex = new ForbiddenOperationException("Not allowed");

        var response = handler.handleForbidden(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void handleEnumMismatch_Returns400() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getValue()).thenReturn("INVALID");
        when(ex.getName()).thenReturn("state");

        var response = handler.handleEnumMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrorMessage().contains("INVALID"));
    }

    @Test
    void handleJpaEntityNotFound_Returns404() {
        var ex = new EntityNotFoundException("Device missing");

        var response = handler.handleJpaEntityNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleEmptyResult_Returns404() {
        var ex = new EmptyResultDataAccessException(1);

        var response = handler.handleEmptyResult(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

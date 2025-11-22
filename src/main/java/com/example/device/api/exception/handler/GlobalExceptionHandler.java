package com.example.device.api.exception.handler;

import com.example.device.api.exception.DeviceAlreadyExistsException;
import com.example.device.api.exception.DeviceNotFoundException;
import com.example.device.api.exception.ForbiddenOperationException;
import com.example.device.api.exception.constants.ErrorCode;
import com.example.device.api.exception.dto.ErrorDetailsDto;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

import java.util.Arrays;

/**
 * A global exception handler for Device API.
 * Wraps application exceptions into unified error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Wraps unexpected exceptions into proper response.
     *
     * @param exception Exception to wrap into proper response.
     * @return ResponseEntity containing HTTP 500 and {@link ErrorDetailsDto}.
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorDetailsDto> handleThrowable(
            Throwable exception,
            HttpServletRequest request) {

        logError("Unexpected error occurred", exception, request);
        var error = ErrorDetailsDto.of(ErrorCode.INTERNAL_ERROR, exception.getMessage());
        return build(error);
    }

    /**
     * Wraps ConstraintViolationException into proper response.
     *
     * @return ResponseEntity with HTTP 400 and {@link ErrorDetailsDto}.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetailsDto> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        logError("Constraint violation", ex, request);
        return build(ErrorDetailsDto.of(ErrorCode.INVALID_REQUEST, ex.getMessage()));
    }

    /**
     * Wraps DeviceAlreadyExistsException into proper 409 response.
     */
    @ExceptionHandler(DeviceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetailsDto> handleAlreadyExists(
            DeviceAlreadyExistsException ex,
            HttpServletRequest request) {

        logError("Duplicate device creation attempt", ex, request);
        return build(ErrorDetailsDto.of(ErrorCode.DEVICE_ALREADY_EXISTS, ex.getMessage()));
    }

    /**
     * Wraps DeviceNotFoundException into proper 404 response.
     */
    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<ErrorDetailsDto> handleNotFound(
            DeviceNotFoundException ex,
            HttpServletRequest request) {

        logError("Device not found", ex, request);
        return build(ErrorDetailsDto.of(ErrorCode.DEVICE_NOT_FOUND, ex.getMessage()));
    }

    /**
     * Wraps Forbidden/Authorization exceptions into HTTP 403 response.
     */
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorDetailsDto> handleForbidden(
            ForbiddenOperationException ex,
            HttpServletRequest request) {

        logError("Forbidden operation", ex, request);
        return build(ErrorDetailsDto.of(ErrorCode.FORBIDDEN_OPERATION, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetailsDto> handleEnumMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        logError("Type mismatch (enum conversion)", ex, request);

        String message = "Invalid value '%s' for parameter '%s'. Valid values: %s"
                .formatted(
                        ex.getValue(),
                        ex.getName(),
                        ex.getRequiredType() != null && ex.getRequiredType().isEnum()
                                ? String.join(", ", Arrays.stream(ex.getRequiredType().getEnumConstants()).map(Object::toString).toList())
                                : "Unknown"
                );

        return build(ErrorDetailsDto.of(ErrorCode.INVALID_REQUEST, message));
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn("Method not supported: {}", ex.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(ErrorDetailsDto.of(ErrorCode.INVALID_REQUEST, "HTTP method not supported"));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetailsDto> handleJpaEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        logError("Entity not found (JPA)", ex, request);
        return build(ErrorDetailsDto.of(ErrorCode.DEVICE_NOT_FOUND, "Device does not exist"));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorDetailsDto> handleEmptyResult(
            EmptyResultDataAccessException ex,
            HttpServletRequest request) {

        logError("No data found in DB (EmptyResultDataAccessException)", ex, request);
        return build(ErrorDetailsDto.of(ErrorCode.DEVICE_NOT_FOUND, "Record not found in database"));
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn("Request binding exception: {}", ex.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(ErrorDetailsDto.of(ErrorCode.INVALID_REQUEST, "Invalid request parameters"));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(ErrorDetailsDto.of(ErrorCode.INVALID_REQUEST, "Malformed JSON request body"));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.error("Validation error occurred: {}", ex.getMessage());

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request data");

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(ErrorDetailsDto.of(ErrorCode.INVALID_REQUEST, message));
    }

    protected ResponseEntity<ErrorDetailsDto> build(ErrorDetailsDto details) {
        return ResponseEntity.status(details.getHttpStatus()).body(details);
    }

    protected void logError(String msg, Throwable ex, HttpServletRequest request) {
        log.error("{} | {} {} | Exception: {}",
                msg,
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);
    }
}

package com.dekraChallenge.dekra_challenge.adapter.in.web;

import com.dekraChallenge.dekra_challenge.adapter.in.web.auth.InvalidCredentialsException;
import com.dekraChallenge.dekra_challenge.api.web.model.ErrorResponse;
import com.dekraChallenge.dekra_challenge.api.web.model.FieldError;
import com.dekraChallenge.dekra_challenge.domain.model.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProductNotFoundException ex,
                                                        HttpServletRequest request) {
        log.warn("Resource not found: {} {} - {}", request.getMethod(),
                request.getRequestURI(), ex.getMessage());
        ErrorResponse error = buildErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        log.debug("Validation failed: {} {} - {} field error(s)", request.getMethod(),
                request.getRequestURI(), ex.getBindingResult().getFieldErrorCount());

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> {
                    FieldError fieldError = new FieldError();
                    fieldError.setField(fe.getField());
                    fieldError.setMessage(fe.getDefaultMessage());
                    return fieldError;
                })
                .toList();

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI());
        error.setFieldErrors(fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                               HttpServletRequest request) {
        log.warn("Bad request: {} {} - {}", request.getMethod(),
                request.getRequestURI(), ex.getMessage());
        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex,
                                                                   HttpServletRequest request) {
        log.warn("Invalid credentials attempt: {} {}", request.getMethod(),
                request.getRequestURI());
        ErrorResponse error = buildErrorResponse(
                HttpStatus.UNAUTHORIZED, "Invalid credentials", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex,
                                                               HttpServletRequest request) {
        log.warn("Resource not found: {} {}", request.getMethod(), request.getRequestURI());

        ErrorResponse error = buildErrorResponse(
                HttpStatus.NOT_FOUND, "Resource not found", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex,
                                                       HttpServletRequest request) {
        log.error("Unexpected error: {} {} - [{}] {}",
                request.getMethod(), request.getRequestURI(),
                ex.getClass().getSimpleName(), ex.getMessage(), ex);
        ErrorResponse error = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(OffsetDateTime.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(message);
        error.setPath(path);
        return error;
    }
}

package com.dekraChallenge.dekra_challenge.adapter.in.web;

import com.dekraChallenge.dekra_challenge.adapter.in.web.auth.InvalidCredentialsException;
import com.dekraChallenge.dekra_challenge.api.web.model.ErrorResponse;
import com.dekraChallenge.dekra_challenge.domain.model.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 *
 * <p>Verifies that:
 * <ul>
 *   <li>unexpected exceptions produce a generic 500 response without leaking internal details;</li>
 *   <li>known business exceptions produce the correct HTTP status and safe message;</li>
 *   <li>invalid credentials produce 401 without internal details.</li>
 * </ul>
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/productos/1");
        when(request.getMethod()).thenReturn("GET");
    }

    @Test
    void handleGeneric_returns_500_without_exposing_internal_details() {
        RuntimeException internalError = new RuntimeException("NullPointerException in secret code");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(internalError, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("Internal server error");
        assertThat(response.getBody().getPath()).isEqualTo("/productos/1");
        // Must NOT contain the internal exception message
        assertThat(response.getBody().getMessage()).doesNotContain("NullPointerException");
        assertThat(response.getBody().getMessage()).doesNotContain("secret code");
    }

    @Test
    void handleNotFound_returns_404_with_product_message() {
        ProductNotFoundException ex = new ProductNotFoundException(99L);

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).contains("99");
    }

    @Test
    void handleIllegalArgument_returns_400() {
        IllegalArgumentException ex = new IllegalArgumentException("username must not be blank");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
    }

    @Test
    void handleInvalidCredentials_returns_401_without_details() {
        when(request.getRequestURI()).thenReturn("/auth/token");
        when(request.getMethod()).thenReturn("POST");
        InvalidCredentialsException ex = new InvalidCredentialsException("Invalid credentials");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidCredentials(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid credentials");
        assertThat(response.getBody().getPath()).isEqualTo("/auth/token");
    }
}

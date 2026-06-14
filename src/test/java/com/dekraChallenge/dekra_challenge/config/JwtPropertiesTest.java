package com.dekraChallenge.dekra_challenge.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Validates the Bean Validation constraints on {@link JwtProperties}: the secret has no default and
 * must be a non-blank string of at least 32 characters, while {@code issuer} and
 * {@code expirationSeconds} keep safe defaults.
 */
class JwtPropertiesTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private static JwtProperties withSecret(String secret) {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(secret);
        return properties;
    }

    @Test
    void should_have_no_default_secret_and_safe_defaults_for_issuer_and_expiration() {
        JwtProperties properties = new JwtProperties();

        assertThat(properties.getSecret()).isNull();
        assertThat(properties.getIssuer()).isEqualTo("dekra-challenge");
        assertThat(properties.getExpirationSeconds()).isEqualTo(3600);
    }

    @Test
    void should_pass_validation_with_a_valid_32_char_secret() {
        JwtProperties properties = withSecret("0123456789-0123456789-0123456789-abc");

        Set<ConstraintViolation<JwtProperties>> violations = validator.validate(properties);

        assertThat(violations).isEmpty();
    }

    @Test
    void should_fail_validation_when_secret_is_null() {
        JwtProperties properties = withSecret(null);

        Set<ConstraintViolation<JwtProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("secret");
    }

    @Test
    void should_fail_validation_when_secret_is_blank() {
        JwtProperties properties = withSecret("   ");

        Set<ConstraintViolation<JwtProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("secret");
    }

    @Test
    void should_fail_validation_when_secret_is_shorter_than_32_chars() {
        JwtProperties properties = withSecret("too-short-secret");

        Set<ConstraintViolation<JwtProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("secret");
    }

    @Test
    void should_fail_validation_when_issuer_is_blank() {
        JwtProperties properties = withSecret("0123456789-0123456789-0123456789-abc");
        properties.setIssuer("  ");

        Set<ConstraintViolation<JwtProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("issuer");
    }

    @Test
    void should_fail_validation_when_expiration_is_below_minimum() {
        JwtProperties properties = withSecret("0123456789-0123456789-0123456789-abc");
        properties.setExpirationSeconds(30);

        Set<ConstraintViolation<JwtProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("expirationSeconds");
    }
}

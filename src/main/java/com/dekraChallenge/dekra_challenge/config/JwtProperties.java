package com.dekraChallenge.dekra_challenge.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {

    @NotBlank
    @Size(min = 32)
    private String secret;

    @NotBlank
    private String issuer = "dekra-challenge";

    @Min(60)
    private long expirationSeconds = 3600;

}

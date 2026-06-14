package com.dekraChallenge.dekra_challenge.adapter.in.web.auth;

import com.dekraChallenge.dekra_challenge.api.web.model.AuthResponse;
import com.dekraChallenge.dekra_challenge.config.DemoUsersProperties;
import com.dekraChallenge.dekra_challenge.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class TokenService {

    private static final String BEARER = "Bearer";

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final DemoUsersProperties demoUsersProperties;

    public TokenService(JwtEncoder jwtEncoder,
                        JwtProperties jwtProperties,
                        DemoUsersProperties demoUsersProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
        this.demoUsersProperties = demoUsersProperties;
    }

    public AuthResponse issueToken(String username, String password) {
        DemoUsersProperties.DemoUser user = demoUsersProperties.findByCredentials(username, password)
                .orElseThrow(() -> {
                    log.warn("Failed authentication attempt for username: {}", username);
                    return new InvalidCredentialsException("Invalid credentials");
                });

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(jwtProperties.getExpirationSeconds());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getUsername())
                .claim("roles", List.of(user.getRole()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        log.info("Token issued for user '{}' with role '{}'", user.getUsername(), user.getRole());

        return new AuthResponse()
                .accessToken(token)
                .tokenType(BEARER)
                .expiresIn(jwtProperties.getExpirationSeconds());
    }
}

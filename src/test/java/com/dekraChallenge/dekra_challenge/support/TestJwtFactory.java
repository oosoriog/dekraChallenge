package com.dekraChallenge.dekra_challenge.support;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public final class TestJwtFactory {

    public static final String ISSUER = "dekra-challenge";

    private static final String WRONG_SECRET = "totally-different-wrong-secret-0123456789-abcdefgh";

    private static final long EXPIRATION_SECONDS = 3600;

    private final String secret;

    public TestJwtFactory(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("TestJwtFactory: secret must not be blank");
        }
        this.secret = secret;
    }

    public String userToken() {
        return token("user", "USER");
    }

    public String adminToken() {
        return token("admin", "ADMIN");
    }

    public String token(String subject, String role) {
        return sign(subject, role, secret);
    }

    public String invalidToken() {
        return sign("user", "USER", WRONG_SECRET);
    }

    private static String sign(String subject, String role, String signingSecret) {
        try {
            Date now = new Date();
            Date expiry = new Date(now.getTime() + EXPIRATION_SECONDS * 1000L);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer(ISSUER)
                    .issueTime(now)
                    .expirationTime(expiry)
                    .claim("roles", List.of(role))
                    .build();

            SignedJWT signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJwt.sign(new MACSigner(signingSecret.getBytes(StandardCharsets.UTF_8)));
            return signedJwt.serialize();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to mint test JWT", ex);
        }
    }
}

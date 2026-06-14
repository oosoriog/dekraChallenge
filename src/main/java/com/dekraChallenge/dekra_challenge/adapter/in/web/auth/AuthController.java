package com.dekraChallenge.dekra_challenge.adapter.in.web.auth;

import com.dekraChallenge.dekra_challenge.api.web.api.AuthApi;
import com.dekraChallenge.dekra_challenge.api.web.model.AuthRequest;
import com.dekraChallenge.dekra_challenge.api.web.model.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public ResponseEntity<AuthResponse> obtainToken(AuthRequest authRequest) {
        return ResponseEntity.ok(
                tokenService.issueToken(authRequest.getUsername(), authRequest.getPassword()));
    }
}

package com.dekraChallenge.dekra_challenge.adapter.in.web.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {

    @Override
    public String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null
                || authentication.getName().isBlank()) {
            throw new IllegalStateException("No authenticated user");
        }
        return authentication.getName();
    }
}

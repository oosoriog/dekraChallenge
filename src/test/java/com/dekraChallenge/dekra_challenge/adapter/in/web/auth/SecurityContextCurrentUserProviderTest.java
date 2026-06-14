package com.dekraChallenge.dekra_challenge.adapter.in.web.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityContextCurrentUserProviderTest {

    private final SecurityContextCurrentUserProvider provider = new SecurityContextCurrentUserProvider();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_return_authenticated_username() {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "n/a", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(provider.currentUsername()).isEqualTo("admin");
    }

    @Test
    void should_throw_when_no_authentication() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(provider::currentUsername)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No authenticated user");
    }

    @Test
    void should_throw_when_authentication_name_blank() {
        Authentication auth = new UsernamePasswordAuthenticationToken("   ", "n/a", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThatThrownBy(provider::currentUsername)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No authenticated user");
    }
}

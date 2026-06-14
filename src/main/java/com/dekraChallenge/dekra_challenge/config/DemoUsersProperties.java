package com.dekraChallenge.dekra_challenge.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Optional;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.security.demo-users")
public class DemoUsersProperties {

    private List<DemoUser> users = List.of(
            new DemoUser("user", "user-password", "USER"),
            new DemoUser("admin", "admin-password", "ADMIN"));

    public Optional<DemoUser> findByCredentials(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        return users.stream()
                .filter(u -> username.equals(u.getUsername()) && password.equals(u.getPassword()))
                .findFirst();
    }

    @Setter
    @Getter
    public static class DemoUser {

        private String username;
        private String password;
        private String role;

        public DemoUser() {
        }

        public DemoUser(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

    }
}

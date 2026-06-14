package com.dekraChallenge.dekra_challenge.config;

import com.dekraChallenge.dekra_challenge.config.DemoUsersProperties.DemoUser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DemoUsersPropertiesTest {

    private final DemoUsersProperties properties = new DemoUsersProperties();

    @Test
    void should_have_default_user_and_admin() {
        List<DemoUser> users = properties.getUsers();

        assertThat(users).hasSize(2);
        assertThat(users).anySatisfy(u -> {
            assertThat(u.getUsername()).isEqualTo("user");
            assertThat(u.getRole()).isEqualTo("USER");
        });
        assertThat(users).anySatisfy(u -> {
            assertThat(u.getUsername()).isEqualTo("admin");
            assertThat(u.getRole()).isEqualTo("ADMIN");
        });
    }

    @Test
    void should_find_admin_by_valid_credentials() {
        Optional<DemoUser> found = properties.findByCredentials("admin", "admin-password");

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo("ADMIN");
    }

    @Test
    void should_find_user_by_valid_credentials() {
        Optional<DemoUser> found = properties.findByCredentials("user", "user-password");

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo("USER");
    }

    @Test
    void should_not_find_when_password_wrong() {
        assertThat(properties.findByCredentials("admin", "wrong")).isEmpty();
    }

    @Test
    void should_not_find_when_username_unknown() {
        assertThat(properties.findByCredentials("ghost", "user-password")).isEmpty();
    }

    @Test
    void should_return_empty_when_username_null() {
        assertThat(properties.findByCredentials(null, "user-password")).isEmpty();
    }

    @Test
    void should_return_empty_when_password_null() {
        assertThat(properties.findByCredentials("user", null)).isEmpty();
    }

    @Test
    void should_allow_overriding_users() {
        DemoUsersProperties custom = new DemoUsersProperties();
        custom.setUsers(List.of(new DemoUser("custom", "pw", "USER")));

        assertThat(custom.getUsers()).hasSize(1);
        assertThat(custom.findByCredentials("custom", "pw")).isPresent();
    }
}

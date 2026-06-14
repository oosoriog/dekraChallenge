# Rule 05 — Security: JWT Bearer (OAuth2 Resource Server) + Local Demo Token Issuer

## Status: MANDATORY (Phase 5)

> **History:** This project first planned HTTP Basic, then (user-approved, 2026-06-12) switched to
> **JWT Bearer Token** authentication. See `memory/project-decisions.md`. JWT is the security
> mechanism; HTTP Basic is no longer used.

Security is **required** by the assessment. All `/productos` endpoints must be protected and
accessible only by authenticated and **authorised** users. Authentication uses **JWT Bearer
tokens** validated locally by Spring Security's **OAuth2 Resource Server**.

> **Dependency gate:** `spring-boot-starter-oauth2-resource-server` (main) and
> `spring-boot-starter-security-test` (test) are NOT in pom.xml yet. Adding them is the first task
> of Phase 5 and **requires approval**.

---

## Approach

- **Mechanism:** Spring Security **OAuth2 Resource Server**, JWT-encoded bearer tokens.
- **Validation:** **local**, using a **symmetric HMAC (HS256) secret**. No external IdP, no JWKS.
- **Token issuance (demo only):** a **local** `POST /auth/token` endpoint issues JWTs for two
  configurable demo users, so evaluators can obtain a token without an external identity provider.
- **Clients call protected endpoints with:** `Authorization: Bearer <jwt>`.

### Demo users (typed configuration, safe local defaults, env-overridable)

| Username | Password (default) | Role |
|---|---|---|
| `user` | `user-password` | `ROLE_USER` |
| `admin` | `admin-password` | `ROLE_ADMIN` |

Credentials come from typed `@ConfigurationProperties` with safe local defaults, overridable via
environment variables later. **Not** stored in a database. **No** registration.

### JWT claims

| Claim | Value |
|---|---|
| `sub` | username |
| `roles` | `["USER"]` or `["ADMIN"]` |
| `iss` | `"dekra-challenge"` |
| `exp` | expiration time (e.g. now + 3600s) |

### Authorization rules

| Method / path | Allowed roles |
|---|---|
| `POST /auth/token` | **public** (no token required) |
| `GET /productos` | `ROLE_USER` or `ROLE_ADMIN` |
| `GET /productos/{id}` | `ROLE_USER` or `ROLE_ADMIN` |
| `POST /productos` | `ROLE_ADMIN` |
| `PUT /productos/{id}` | `ROLE_ADMIN` |
| `DELETE /productos/{id}` | `ROLE_ADMIN` |

### Role mapping

The JWT `roles` claim (`["USER"]`/`["ADMIN"]`) is mapped to Spring authorities
`ROLE_USER`/`ROLE_ADMIN` via a custom `JwtAuthenticationConverter` (+
`JwtGrantedAuthoritiesConverter` or equivalent). Use `hasRole("ADMIN")` / `hasAnyRole("USER","ADMIN")`.

### Illustrative filter chain (verify Spring Boot 4 / Security 7 before implementing)

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())                       // stateless REST API
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, "/auth/token").permitAll()
            .requestMatchers(HttpMethod.GET, "/productos/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/productos/**").hasRole("ADMIN")   // POST/PUT/DELETE
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
    return http.build();
}
```

---

## Audit User Propagation (soft delete `deletedBy`)

`deletedBy` is **mandatory** on delete and MUST be the authenticated username taken from the JWT
**subject** (`sub`) — **never** from the request body, query string, or a header.

Expected delete flow:

1. The `CurrentUserProvider` reads the authenticated principal name (JWT `sub`) from the
   `SecurityContext` (`Authentication.getName()`).
2. The controller passes that username into `ProductoService.eliminar(id, username)`.
3. Persistence stores `deleted = true`, `deletedAt = now`, `deletedBy = <jwt sub>`.

> **Phase 4 → Phase 5 transition:** Phase 4 provided a placeholder `SystemCurrentUserProvider`
> ("phase4-system"). Phase 5 **replaces** it with a security-aware `CurrentUserProvider` that reads
> the JWT subject. There must be exactly one `CurrentUserProvider` bean after Phase 5.

---

## `/auth/token` — local demo token issuer (non-production)

- **Purpose:** assessment usability only — lets evaluators obtain a JWT without an external IdP.
- **Public** endpoint (no bearer token required); validates the posted demo credentials against the
  configured demo users and returns a signed JWT.
- Request: `{ "username": "...", "password": "..." }` (`AuthRequest`).
- Response: `{ "accessToken": "<jwt>", "tokenType": "Bearer", "expiresIn": 3600 }` (`AuthResponse`).
- Invalid credentials → `401`.
- **Explicitly documented as non-production identity management.** In production this local issuer
  would be replaced by an external IdP/OIDC provider. The app must NOT become a full authorization
  server.

---

## What NOT to Do

- Do NOT use Keycloak, Auth0, Okta or any external identity provider.
- Do NOT use Spring Authorization Server.
- Do NOT implement OAuth2 client login flows, refresh tokens, logout, or user registration.
- Do NOT store users in a database (demo users come from typed configuration).
- Do NOT read `deletedBy` (or any audit user) from the request body, query, or header.
- Do NOT expose audit fields in API responses.
- Do NOT commit a real secret; use a safe demo default that is overridable via environment variable.
- Verify Spring Security 7 / Spring Boot 4 patterns — do not copy Boot 2/3 security config.

---

## Testing

- `POST /auth/token` returns a token for valid admin (and user) credentials.
- `POST /auth/token` rejects invalid credentials (401).
- No token → 401 on `/productos`; invalid/expired token → 401.
- USER token can read (`GET`); USER token cannot write (`POST/PUT/DELETE` → 403).
- ADMIN token can create/update/delete.
- DELETE stores `deletedBy` equal to the JWT subject.
- Tests mint their own JWTs (HS256 with the test secret) or call `/auth/token`; use
  `spring-security-test` helpers where useful.

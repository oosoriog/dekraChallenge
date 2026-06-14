# Rule 09 — DevOps: Docker, CI/CD & Coverage

## Status: MANDATORY (Phase 7 delivery; coverage in Phase 6)

> **Changed decision:** Docker, CI/CD and coverage enforcement were previously optional.
> They are now **mandatory final delivery requirements**. The application database remains
> **H2 in-memory** even when containerised.

---

## Mandatory Delivery Artifacts

| Concern | Requirement |
|---|---|
| Dockerfile | Build/run the Spring Boot app (multi-stage: builder + JRE runtime). |
| docker-compose.yml | Able to run the application (H2 in-memory; self-contained). |
| README | Build, test and run-locally instructions (incl. `./mvnw spring-boot:run`, Docker, H2 console, credentials, `app.tax.type`). |
| CI/CD | **GitHub Actions** pipeline unless another CI platform is explicitly selected. |
| Coverage | **JaCoCo** (or equivalent Maven-compatible tooling) reporting and enforcement. |

> **Dependency/plugin gates:** `openapi-generator-maven-plugin` (Phase 1), `com.h2database:h2`,
> `spring-boot-starter-security` + `spring-security-test`, `spring-boot-starter-aop`, and the
> JaCoCo plugin are NOT in pom.xml yet. Each is added **with explicit approval** at its phase.
> PostgreSQL/Flyway/Testcontainers deps must be **removed** (gated cleanup task).

---

## CI Pipeline (minimum stages)

1. Checkout.
2. Java setup (JDK 21 dev runtime; build targets Java 17 bytecode).
3. Maven build.
4. Tests.
5. Coverage report / enforcement (fail under threshold).
6. (Optional) Docker image build.

---

## Coverage Targets (MANDATORY)

- At least **80% line coverage**.
- At least **80% branch/condition coverage** where the tooling supports it.
- Coverage must be **meaningful** — do NOT add superficial tests just to inflate the number.

---

## Docker Notes

- The container runs the Spring Boot app with H2 in-memory — **no external database service**.
- Keep `docker-compose.yml` simple: a single app service is sufficient (no PostgreSQL, no Keycloak).
- Multi-stage Dockerfile: build with the Maven wrapper, run on a slim JRE base image.

---

## Priority

All **mandatory functional** requirements (CRUD, dynamic query, security, AOP, tax
calculation, soft delete) take priority over delivery packaging. Implement and test the
functional scope first, then complete Docker/CI/coverage in Phases 6–7. Everything in this
rule is mandatory for final delivery; nothing here is "nice to have".

---

## What NOT to Do

- Do NOT add complex multi-service Docker Compose (no PostgreSQL, no Keycloak).
- Do NOT add JaCoCo or other plugins to pom.xml without approval (gated to their phase).
- Do NOT block functional implementation waiting on infrastructure — but do NOT skip it either.
- The application must still work without Docker: `./mvnw spring-boot:run`.

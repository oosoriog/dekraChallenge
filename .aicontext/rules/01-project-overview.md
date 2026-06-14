# Rule 01 — Project Overview

## Purpose

This is a **technical assessment** submitted for a recruitment process at Dekra.
The goal is to demonstrate professional-grade Java development skills:
clean architecture, testability, maintainability and good engineering judgement.

**Clarity and reviewability are the primary success criteria.**
Over-engineering, premature optimisation or unnecessary complexity are penalised.

---

## Assessment Requirements Summary

Build a RESTful CRUD application for a **Producto** entity with:
- API-first design: OpenAPI contract + OpenAPI Generator (DTOs/models + API interfaces)
- Spring Security (HTTP Basic, in-memory users)
- H2 in-memory database with lightweight soft delete + audit fields
- AOP execution-time logging aspect
- Functional interface for tax calculation (IVA 21% / ITBIS 18%)
- Property-based calculator selection (`app.tax.type`), tax exposed in responses
- Dynamic query endpoint (filter by product properties) — **mandatory**
- Mandatory delivery: Docker, docker-compose, GitHub Actions CI, JaCoCo coverage (≥80%)

---

## Stack Summary

| Component | Technology |
|---|---|
| Language | Java 21 (dev JDK); bytecode targets Java 17 (compiler release 17) |
| Framework | Spring Boot 4.0.7 |
| Web | Spring MVC (`spring-boot-starter-webmvc`) |
| Persistence | Spring Data JPA + Hibernate |
| Database | H2 in-memory |
| Validation | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Security | Spring Security — HTTP Basic, in-memory users |
| AOP | Spring AOP (execution-time logging) |
| Tax logic | Functional interface + polymorphic implementations |
| API docs | OpenAPI 3 contract (`openapi/productos-api.yaml`) + OpenAPI Generator; SpringDoc Swagger UI available |
| Boilerplate | Lombok |
| Tests | JUnit 5 + MockMvc; JaCoCo coverage (≥80%) |
| Build | Maven Wrapper (`mvnw`) |
| Delivery | Docker + docker-compose + GitHub Actions CI |

---

## Package Root

```
com.dekraChallenge.dekra_challenge
```

Suggested sub-packages (lightweight hexagonal):

```
com.dekraChallenge.dekra_challenge
├── domain/           ← Producto entity, tax interfaces, domain logic
├── application/      ← Service/use-case layer
├── adapter/
│   ├── in/web/       ← REST controllers, DTOs
│   └── out/persistence/ ← JPA repository
├── config/           ← Spring configuration (security, tax bean selection)
└── aspect/           ← AOP execution-time logging
```

---

## Assessment Constraints

- The project must be runnable with `./mvnw spring-boot:run` (no external DB needed — H2 in-memory).
- It must also be runnable via Docker / docker-compose (still H2 in-memory; no PostgreSQL, no Keycloak).
- All decisions must be **explainable** in a live review session.
- Prefer standard Spring Boot idioms; avoid magic, custom frameworks or proprietary libraries.
- Favour clarity, maintainability and reviewability over breadth of features.
- Do NOT use Java language features or APIs unavailable in Java 17.
- Money uses `BigDecimal`; never `double`/`float`.

---

## Dependency Reality (pom.xml is the source of truth)

Some required libraries are **not yet declared** and must be added **with explicit
approval** at the start of their phase (see `implementation-plan.md` / `task-backlog.md`):

| Need | Dependency / Plugin | Phase | Status |
|---|---|---|---|
| OpenAPI codegen | `openapi-generator-maven-plugin` | 1 | ABSENT |
| H2 database | `com.h2database:h2` | 1 or 3 | ABSENT |
| Spring Security | `spring-boot-starter-security` (+ `spring-security-test`) | 5 | ABSENT |
| AOP | `spring-boot-starter-aop` | 6 | ABSENT |
| Coverage | `jacoco-maven-plugin` (or equivalent) | 6 | ABSENT |
| Java 17 target | `maven.compiler.release=17` | 1 | not explicit |

Present and reused: webmvc, data-jpa, validation, actuator, springdoc, lombok.
Present but to be **removed** (gated cleanup task, Phase 1 or 3): PostgreSQL driver, Flyway
(`spring-boot-starter-flyway`, `flyway-database-postgresql`), Testcontainers
(`spring-boot-testcontainers`, `testcontainers-*`) — plus the obsolete Testcontainers/Postgres
test scaffolding under `src/test`.

---

## Spring Boot 4 Compatibility Note

This project uses **Spring Boot 4.0.7**, which is built on **Spring Framework 7** and **Jakarta EE 11**.

Agents must:
- Avoid copying outdated Spring Boot 2/3 examples without checking compatibility.
- Use `jakarta.*` packages (not `javax.*`).
- Be aware that Spring Security configuration has changed significantly since Boot 2/3.
- Verify Servlet API compatibility (Servlet 6.1).
- Not update dependency versions without explicit approval — if the project compiles as-is, leave it.

**If a pattern feels familiar from Spring Boot 2 tutorials, double-check it works on Spring Boot 4.**

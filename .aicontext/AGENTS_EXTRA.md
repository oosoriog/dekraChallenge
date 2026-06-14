# AGENTS_EXTRA.md - Extra Agent Instructions

This file contains cross-cutting conventions for AI coding assistants working on this project.

It is intended as a stable project context for future maintenance and feature development. It is not
a log of the original implementation process.

---

## How to use this context

Before making changes, understand the relevant project context under `.aicontext/`.

Recommended reading depending on the task:

- `.aicontext/rules/01-project-overview.md`
- `.aicontext/rules/02-architecture-hexagonal.md`
- `.aicontext/rules/03-api-first-openapi.md`
- `.aicontext/rules/04-domain-model.md`
- `.aicontext/rules/05-security-spring-security.md`
- `.aicontext/rules/06-persistence-h2-audit-soft-delete.md`
- `.aicontext/rules/07-testing-strategy.md`
- `.aicontext/rules/08-observability.md`
- `.aicontext/rules/09-devops-cicd.md`
- `.aicontext/rules/10-ai-agent-working-rules.md`

Use `.aicontext/agents/` only as specialised role guidance when the task clearly matches one of
those areas.

Use `.aicontext/prompts/` as reusable templates for common tasks such as implementing a feature,
reviewing code, updating OpenAPI or generating tests.

Use `.aicontext/tools/` as command references, but always verify commands against the current
`Makefile`, `pom.xml`, `Dockerfile` and `docker-compose.yml` before relying on them.

---

## General conduct

- Prefer explicit, readable code over clever abstractions.
- Keep changes small, focused and reviewable.
- Do not introduce new infrastructure unless explicitly requested.
- Do not add dependencies, plugins or large abstractions without a clear reason.
- Update tests when behaviour changes.
- Keep documentation short and practical.
- Communicate assumptions clearly when they affect the implementation.
- Do not modify generated sources manually.

---

## Project principles

- This is a small CRUD technical assessment, not a full production platform.
- Some production-oriented choices are intentional, but they should remain simple and local.
- The public API contract is in Spanish because it follows the challenge requirements.
- Internal code uses English names for maintainability.
- The domain must remain independent from Spring, JPA, generated OpenAPI DTOs and persistence details.
- Generated OpenAPI DTOs and API interfaces belong only at the REST boundary.
- JPA entities must not leak outside the persistence adapter.
- Audit fields must not be exposed in API responses.
- Calculated tax values must be computed on read, not persisted.

---

## Code style

| Rule | Detail |
|---|---|
| Java version | Java 17 target. Do not use language features or APIs newer than Java 17. |
| Money | Use `BigDecimal`; never `double` or `float` for prices, taxes or final amounts. |
| Rounding | Tax and final price values must use scale 2 and `HALF_UP` rounding. |
| Lombok | Allowed for boilerplate reduction when it keeps the code readable. |
| Null handling | Prefer explicit handling. Avoid raw `null` in the domain layer when possible. |
| Naming | Packages use lowercase. Classes use PascalCase. Avoid unnecessary abbreviations. |
| Imports | Do not use wildcard imports. |
| Line length | Keep lines reasonably short; target 120 characters maximum. |

---

## Architecture rules

- Keep the current hexagonal architecture:
  - `domain`: business model, tax logic and repository ports.
  - `application`: use cases and orchestration.
  - `adapter.in.web`: REST controllers, generated API interfaces, DTO mapping and web errors.
  - `adapter.out.persistence`: JPA entities, Spring Data repositories and persistence adapters.
  - `config`: Spring configuration and typed properties.
  - `aspect`: cross-cutting execution-time logging.
- Domain code must not import Spring, JPA, Jakarta validation annotations, generated DTOs or web classes.
- Application services should orchestrate use cases, not contain framework-specific code.
- Web adapters translate between generated OpenAPI DTOs and application/domain models.
- Persistence adapters translate between JPA entities and domain models.
- Keep business logic out of mappers when it belongs in services or domain components.

---

## API rules

- The OpenAPI contract is the source of truth for the public API.
- Public paths and JSON fields must stay in Spanish unless the contract requirement changes.
- If the API changes, update `src/main/resources/static/openapi.yaml` first.
- Do not manually edit generated OpenAPI classes.
- Generated DTOs must not enter the domain layer.
- Keep Swagger UI usable for manual testing.

---

## Security rules

- The application uses JWT Bearer authentication.
- `/auth/token` is a local demo token issuer for assessment usability only.
- Do not introduce Keycloak, Auth0, Spring Authorization Server, OAuth2 client login, refresh tokens,
  logout, registration or database-backed users unless explicitly requested.
- The JWT signing secret must never be hardcoded.
- Secrets, passwords, raw JWTs and Authorization headers must never be logged.
- `deletedBy` and audit users must come from the authenticated principal, never from request body,
  query parameters or custom headers.

---

## Persistence rules

- The database is H2 in-memory for this assessment.
- Do not replace H2 with PostgreSQL, MySQL or another external database unless explicitly requested.
- Product deletion is soft delete, not physical delete.
- Soft-deleted products must not appear in normal reads or searches.
- Audit fields are persisted but not returned by the public API.
- Do not introduce Envers, history tables or external audit frameworks unless explicitly requested.

---

## Tax rules

- Supported tax types are `IVA` and `ITBIS`.
- The active tax calculator is selected through configuration.
- Do not expose multiple taxes at once.
- Do not allow per-request tax selection unless explicitly requested.
- Do not persist calculated tax amount or final price.
- Persist only the base product price.
- Tax amount and final price are calculated when products are returned by the API.

---

## Testing and quality rules

Before considering a change complete, run the most relevant verification command.

For normal test execution:

```bash
./mvnw clean test
```

For full quality validation:

```bash
./mvnw clean verify -Pquality
```

The quality profile is expected to run Checkstyle and enforce JaCoCo coverage limits.

If Docker-related files changed, also validate the Docker flow.

If OpenAPI changes, verify that generated sources compile and the affected API tests pass.

---

## Docker and CI rules

- Keep Docker Compose as a single self-contained service unless explicitly requested otherwise.
- Do not add external services such as PostgreSQL, Keycloak or Redis unless explicitly requested.
- The JWT secret must be injected through environment variables or local `.env`, never hardcoded.
- GitHub Actions should verify the Maven build and Docker image build.
- Do not add deployment steps or registry publishing unless explicitly requested.

---

## Forbidden actions without explicit approval

- Adding, removing or upgrading dependencies.
- Changing the Java target version.
- Replacing H2 with an external database.
- Introducing external identity providers.
- Adding proprietary or company-specific libraries.
- Moving generated DTOs into the domain.
- Adding framework annotations to the domain model.
- Persisting calculated tax values.
- Reading audit users from request data.
- Exposing audit fields in API responses.
- Removing tests without replacing the coverage.
- Committing secrets, tokens, passwords or local credentials.
- Modifying generated source files manually.
- Expanding the project into a full production platform.

---

## Commit convention

Use Conventional Commits when commits are requested:

```text
<type>(<scope>): <subject>
```

Examples:

```text
feat(api): add product search endpoint
fix(security): reject requests without bearer token
test(tax): cover ITBIS calculation
docs(readme): simplify docker instructions
ci(actions): add docker image build check
```

Types:

- `feat`
- `fix`
- `test`
- `docs`
- `refactor`
- `chore`
- `ci`

---

## Interaction with the user

- Ask for clarification only when the requirement is genuinely ambiguous.
- Do not ask for permission to follow these rules; they are project conventions.
- When changing code, summarise what changed and which checks were run.
- If a requested change conflicts with this context, explain the trade-off before applying it.

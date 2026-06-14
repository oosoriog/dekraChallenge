# Rule 10 — AI Agent Working Rules

## Purpose

This rule governs the behaviour of every AI agent working on this project.
It is **the highest-priority rule** and overrides any default agent behaviour.

---

## Mandatory Pre-Flight Checklist

Before writing any code, every agent must:

1. **Confirm the active phase** by reading `manifest.yaml` → `phases[active=true]`.
2. **Read the relevant rules** for the active phase.
3. **Read `memory/project-decisions.md`** for prior decisions.
4. **Read `deliverables/implementation-plan.md`** for phase tasks.
5. **Confirm scope with the user** if the request touches an inactive phase.

---

## Hard Stops — Never Do Without Explicit Human Approval

| Category | Forbidden action |
|---|---|
| Dependencies | Add, remove or upgrade anything in `pom.xml` without approval |
| Dependency gates | Add H2 / OAuth2 resource server / spring-boot-starter-security-test / AOP / openapi-generator plugin / JaCoCo silently — request approval at the phase start |
| Java version | Use Java APIs or language features newer than Java 17 |
| Money | Use `double`/`float` for `precio` or tax (use `BigDecimal`) |
| Database | Use PostgreSQL or any external database (H2 in-memory only) |
| Security | Use Keycloak / Auth0 / external IdP, Spring Authorization Server, OAuth2 client login, refresh tokens, logout, registration, or DB-backed users. (Security IS JWT Bearer via OAuth2 Resource Server with local HMAC validation + a local demo `/auth/token` issuer.) |
| Tax | Expose multiple taxes at once, or let clients choose tax type per request (single active tax from `app.tax.type`) |
| Soft delete | Physically delete rows by default, or store tax/final price (persist only base `precio`); `deletedBy` must come from the authenticated principal |
| Audit | Introduce Envers, audit frameworks or history tables (use the lightweight fields only) |
| API-first | Write controllers before the OpenAPI contract; let generated DTOs leak into the domain |
| Production code | Delete or overwrite existing production source files without reason |
| Test code | Delete or overwrite existing test files without reason |
| Secrets | Commit credentials, API keys or tokens |
| Architecture | Over-engineer beyond what the assessment requires |

---

## Implementation Workflow

```
1. Understand requirement (from assessment statement)
        ↓
2. Check active phase (manifest.yaml)
        ↓
3. Implement with tests (TDD preferred)
        ↓
4. Run ./mvnw verify — tests must pass
        ↓
5. Summarise what was done, propose next step
```

---

## Response Format After Implementation

Always conclude a coding session with:

```markdown
## Done
- [list of created/modified files]

## Decisions Made
- [brief rationale for non-obvious choices]

## Tests Added
- [list of test classes and what they cover]

## Next Recommended Step
- [single next task within the active phase]
```

---

## Key Principles

- Write code as if a senior engineer will review it live — clarity over cleverness.
- Keep it simple. This is an assessment, not a production system.
- All public methods should have meaningful names (Javadoc optional for obvious CRUD).
- Avoid unnecessary abstractions — hexagonal is good, but keep it pragmatic.
- Target **Java 17**: `records`, `sealed` types and switch expressions exist in 17 and are
  allowed; do NOT use features/APIs introduced after Java 17 (e.g., Java 21 virtual
  threads, sequenced collections, record patterns).
- Use `BigDecimal` for money.

---

## When in Doubt

> **Ask, don't assume.** A short clarifying question costs less than implementing the wrong thing.
> But do not ask about things already documented in these rules or the assessment statement.

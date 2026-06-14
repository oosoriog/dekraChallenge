# Rule 07 — Testing Strategy

## Principle

**Every implementation unit must be accompanied by tests in the same phase.**
Tests are not optional and are not deferred to a later phase.
A feature is not complete until its tests pass.

---

## Test Pyramid

```
         ┌────────────────────┐
         │   E2E / Manual     │  (not required for this assessment)
         ├────────────────────┤
         │  Integration Tests │  (MockMvc + H2, security tests)
         ├────────────────────┤
         │    Unit Tests      │  ← Primary focus
         └────────────────────┘
```

---

## Unit Tests (Phase 2+)

- **Scope:** Domain entity (Producto), tax calculators, property-based selection, service logic.
- **Framework:** JUnit 5 + Mockito.
- **Location:** `src/test/java/.../domain/` and `.../application/`.
- **Rules:**
  - No Spring context loaded — plain `new` instantiation.
  - No database.
  - Fast.
  - Cover happy path + edge cases + invariant violations.
  - Descriptive method names: `should_{expected}_when_{condition}`

---

## Integration Tests (Phase 4+)

- **Scope:** REST controllers, end-to-end CRUD flows.
- **Framework:** `@SpringBootTest` + MockMvc OR `@WebMvcTest` for controller slices.
- **Database:** H2 in-memory (the same as production — no Testcontainers needed).
- **Security:** Use `@WithMockUser` or `.with(httpBasic("user","pass"))` from Phase 5.
- **Rules:**
  - Test full request/response cycle.
  - Assert HTTP status codes explicitly.
  - Assert response body content.

---

## Slice Tests

| Slice | Annotation | When |
|---|---|---|
| Controller only | `@WebMvcTest` | Testing HTTP mapping, validation, error handling |
| Repository only | `@DataJpaTest` | Testing JPA queries (with H2) |
| Full context | `@SpringBootTest` | End-to-end flows |

---

## Coverage Target (MANDATORY)

- **Mandatory: at least 80% line coverage AND at least 80% branch/condition coverage**
  (where supported), enforced with JaCoCo (or equivalent) in Phase 6.
- Prioritise coverage of: domain logic, tax calculation (exact `BigDecimal`), CRUD operations,
  dynamic query, security, soft delete, error handling.
- Do NOT write low-value tests only to inflate the percentage.

---

## Naming Conventions

- Test class: `{ClassUnderTest}Test` for unit, `{ClassUnderTest}IT` for integration.
- Test method: `should_{expected_behaviour}_when_{condition}`.

---

## What NOT to Do

- Do not use Testcontainers (H2 is the real DB — no containers needed).
- Do not mock domain objects — test them directly.
- Do not write tests that only verify Lombok or framework behavior.
- Do not skip tests on CI.

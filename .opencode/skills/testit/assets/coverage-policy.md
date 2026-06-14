# Coverage Policy

## Final Target

- Line coverage: >= 80%.
- Branch/condition coverage: >= 80% when available.

## Quality Rule

Coverage is a signal, not the goal by itself.

Do not add tests that only execute code without verifying behavior.

## Priority

1. Domain rules.
2. Application use cases.
3. Validation failures and edge cases.
4. Error handling.
5. REST adapter behavior.
6. Security behavior.
7. Persistence behavior.
8. AOP wiring where practical.

## Acceptable Exclusion Candidates

Only propose exclusions. Do not apply them without approval.

- generated code;
- application bootstrap class;
- trivial configuration classes;
- DTOs without behavior;
- code that is not valuable to test directly.

## Maven/Jacoco Guidance

If no coverage tool exists and coverage enforcement is requested, propose JaCoCo Maven Plugin.

Ask before editing `pom.xml` unless the active phase explicitly authorizes build changes.

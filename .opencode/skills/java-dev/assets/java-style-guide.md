# Java Style Guide

## Version policy

- Use the project JDK for local development.
- Keep source/bytecode compatibility with the agreed target version.
- Do not use APIs or language features newer than the target release.

## Naming

- Use descriptive class, method, and variable names.
- Use domain language from the assessment.
- Avoid abbreviations unless they are common in the domain.

## Domain code

- Keep domain independent from frameworks.
- Validate invariants close to the domain model.
- Use `BigDecimal` for money.
- Avoid `double` and `float` for monetary calculations.
- Make rounding explicit.

## Spring code

- Prefer constructor injection.
- Avoid field injection.
- Keep controllers thin.
- Keep business orchestration in application services.
- Keep persistence behind adapters/ports when using hexagonal architecture.
- Do not expose JPA entities in REST responses.

## Error handling

- Use clear exceptions for domain/application errors.
- Convert exceptions to REST responses in infrastructure.
- Keep error responses consistent.

## Testing

- Write behavior-focused tests.
- Cover happy paths and failure paths.
- Keep tests deterministic.
- Avoid tests that assert implementation details with no business value.

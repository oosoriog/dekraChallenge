# java-dev

Portable Java/Spring Boot implementation skill for phase-based technical assessment development.

## Goal

Implement approved phases or tasks one at a time while respecting:

- `AI_CONTEXT.md`
- `.aicontext/`
- phase plans
- task backlog
- Java/Spring Boot best practices
- meaningful tests
- Maven validation

## Typical usage

```text
Use the assessment-java-implementer skill.
Implement Phase 1 only.
```

```text
Use the assessment-java-implementer skill.
Implement task 3.2 from Phase 3 only.
```

```text
Use the assessment-java-implementer skill in review-before-code mode.
Analyze Phase 4 and wait before implementing.
```

## What this skill does

- Reads the project AI context.
- Reads the selected phase plan.
- Inspects the current codebase.
- Implements only the requested phase/task.
- Adds meaningful tests.
- Runs Maven validation.
- Updates task tracking after success.
- Stops before the next phase.

## What this skill does not do

- It does not perform initial planning.
- It does not create deep technical plans.
- It does not implement multiple future phases at once.
- It does not update project decisions without user approval.
- It does not introduce proprietary or unavailable frameworks.
- It does not use external issue trackers or unavailable integrations.

## Recommended workflow

```text
assessment-research-plan
↓
assessment-tech-plan
↓
assessment-java-implementer
↓
assessment-test-engineer
```

## Current assessment profile

The included defaults are tuned for a Product CRUD Java/Spring Boot assessment:

- Java 21 development JDK with Java 17 compatibility.
- Spring Boot.
- Maven.
- H2 in-memory database.
- Product CRUD endpoints.
- Spring Security.
- AOP execution-time logging.
- Tax calculator polymorphism.
- Optional dynamic query only after mandatory scope.

Repository context always wins over these defaults.

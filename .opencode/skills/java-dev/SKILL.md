---
name: java-dev
description: Implement Java Spring Boot project phases safely, following AI context, phase plans, coding guidelines, tests, and validation commands.
agent: agent
argument-hint: "[phase/task] [--review-before-code|--automatic]"
---

## Purpose

Use this skill to implement Java and Spring Boot technical assessment tasks phase by phase, based on the project's AI context, planning documents, technical plans, and task backlog.

This skill is intentionally portable and project-focused. It is designed for standard Java, Maven, Spring Boot, REST APIs, persistence, security, AOP, and testing workflows.

It must not introduce proprietary frameworks, unavailable platform integrations, external issue-tracker workflows, or assumptions that are not present in the repository.

## Core Contract

The `.aicontext/` directory is the source of truth for project decisions, phase order, task backlog, architecture rules, and implementation boundaries.

Before implementing anything, the agent must read:

```text
AI_CONTEXT.md
.aicontext/manifest.yaml
.aicontext/AGENTS_EXTRA.md
.aicontext/rules/*.md
.aicontext/memory/project-decisions.md
.aicontext/deliverables/analysis-and-refinement.md
.aicontext/deliverables/implementation-plan.md
.aicontext/deliverables/task-backlog.md
.aicontext/phases/*.md
```

If any of these files are missing, continue with the files that exist and report the missing context before coding. Do not invent missing decisions.

## Required Behavior

The agent must:

1. Identify the selected phase and selected tasks.
2. Read the relevant phase file under `.aicontext/phases/`.
3. Check explicit in-scope and out-of-scope items.
4. Inspect the existing codebase before modifying files.
5. Implement only the selected phase or selected tasks.
6. Add or update meaningful tests for the implemented behavior.
7. Run the required Maven validation command.
8. Mark completed tasks in the relevant `.aicontext` backlog or phase file only after implementation succeeds.
9. Report all changes, tests, commands, assumptions, and next recommended step.

## Context Update Policy

The agent may update progress checkboxes, task completion markers, and implementation notes in existing planning documents after completing a task.

The agent must ask the user before changing project decisions, architecture rules, phase order, scope, or accepted technologies.

Allowed without asking after successful implementation:

```text
- Marking a task as completed in .aicontext/deliverables/task-backlog.md
- Marking a task as completed in the selected .aicontext/phases/*.md file
- Adding a short implementation note to the selected phase file
- Adding a short validation result to the selected phase file
```

Must ask before updating:

```text
- .aicontext/memory/project-decisions.md
- .aicontext/rules/*.md
- .aicontext/manifest.yaml phase order or active phase model
- architecture decisions
- dependency decisions
- scope changes
- optional feature activation
- security strategy changes
- persistence strategy changes
- Java/Spring/Maven version decisions
```

If a context update is needed, report the proposed change and wait for user approval.

## When to Use

Use this skill when:

- a phase has an approved plan;
- a technical plan exists for the selected phase;
- a specific implementation task must be executed;
- code must be written or modified;
- tests must be added as part of an implementation phase;
- the task backlog must be advanced safely.

Typical workflow:

```text
Research/plan completed
↓
Tech plan completed
↓
Use this skill for one phase or task
↓
Run tests
↓
Update task tracking
↓
Stop for review
```

## When Not to Use

Do not use this skill to:

- perform initial project research;
- create the initial plan;
- create a detailed technical plan without implementing;
- chase coverage-only improvements without a test-specific request;
- rewrite architecture without approval;
- add optional features before mandatory scope is done;
- implement multiple future phases at once;
- introduce new tools not already approved.

Use a planning skill for functional planning, a technical planning skill for detailed implementation design, and a test engineering skill for coverage-focused work.

## Execution Modes

### Single Phase Mode

Implement exactly one approved phase.

Example:

```text
Use the assessment-java-implementer skill.
Implement Phase 2 only.
```

### Single Task Mode

Implement exactly one selected task from a phase.

Example:

```text
Use the assessment-java-implementer skill.
Implement task 3.2 from Phase 3 only.
```

### Fix Mode

Fix a failing build, failing test, or clearly scoped bug introduced in the current or previous phase.

Example:

```text
Use the assessment-java-implementer skill.
Fix the failing Phase 3 controller tests only.
```

### Review Before Code Mode

Analyze the selected task, list the intended modifications, and wait for user approval before coding.

Example:

```text
Use the assessment-java-implementer skill in review-before-code mode.
Analyze Phase 4 and wait before implementing.
```

## Mandatory Implementation Workflow

### Step 1 - Read Context

Read the AI context and selected phase plan.

Confirm:

- selected phase;
- selected tasks;
- in-scope items;
- out-of-scope items;
- tests expected;
- Maven validation command;
- task tracking files to update.

### Step 2 - Inspect Existing Code

Inspect:

```text
pom.xml
src/main/**
src/test/**
application*.yml
application*.properties
```

Do not assume package names, dependencies, or existing patterns.

### Step 3 - Produce a Short Implementation Intent

Before editing, state:

```text
- files expected to be created or modified;
- dependencies expected to be added or changed, if any;
- tests expected to be added or changed;
- out-of-scope items that will not be touched.
```

If review-before-code mode is active, stop and wait for approval.

### Step 4 - Implement Minimally

Implement the smallest coherent slice that satisfies the selected task.

Rules:

- no speculative features;
- no future phases;
- no optional endpoint unless explicitly selected;
- no unnecessary abstractions;
- no generated code unless explicitly approved;
- no broad refactors unrelated to the selected task.

### Step 5 - Add Tests

Every implementation phase must include relevant tests unless the phase plan explicitly says otherwise.

Tests must be meaningful and should cover:

- successful behavior;
- validation failures;
- not-found/error cases;
- security behavior when security is in scope;
- persistence behavior when persistence is in scope;
- boundary cases for monetary calculations.

### Step 6 - Validate

Run the validation command defined in the selected phase plan. If none is defined, run:

```bash
mvn clean test
```

If tests fail, fix only issues related to the selected phase.

Do not hide failing tests.

### Step 7 - Update Task Tracking

After successful validation, update task checkboxes and implementation notes in the selected `.aicontext` task documents.

Do not update project decisions or rules without user approval.

### Step 8 - Report and Stop

Return a concise implementation report and stop.

Do not continue into the next phase unless explicitly requested.

## Java Development Guidelines

### Java Version

Use the project's agreed Java policy.

For the current assessment profile:

- development JDK may be Java 21;
- code must remain Java 17-compatible;
- Maven compiler release should target 17 when configured;
- do not use Java APIs or language features unavailable in Java 17.

### General Java Style

Prefer:

- clear names;
- small methods;
- immutable values where practical;
- constructor validation for domain invariants;
- `BigDecimal` for money;
- explicit rounding for monetary calculations;
- exceptions that express business errors clearly;
- simple, readable code over clever abstractions.

Avoid:

- `double` or `float` for money;
- null-heavy APIs when optionality should be explicit;
- excessive inheritance;
- utility classes for domain behavior that belongs in the domain;
- reflection-heavy code without need;
- broad unchecked casts;
- swallowing exceptions.

### Spring Boot Style

Prefer:

- constructor injection;
- explicit configuration properties when useful;
- standard Spring Boot auto-configuration where suitable;
- REST DTOs separated from domain models;
- persistence entities separated from domain models;
- exception handlers for REST error responses;
- simple security configuration for the assessment scope.

Avoid:

- field injection;
- putting framework annotations in the domain model;
- putting JPA entities in the domain layer;
- exposing persistence entities directly through REST;
- mixing controller, persistence, and business logic in the same class;
- adding infrastructure before it is needed by the selected phase.

### Hexagonal Architecture Guidelines

The architecture should be lightweight and pragmatic.

Recommended separation:

```text
domain/
  model/
  service/
  exception/
application/
  service/
  port/in/
  port/out/
infrastructure/
  adapter/in/rest/
  adapter/out/persistence/
  config/
  aspect/
```

This structure may be adjusted to the existing codebase, but the dependency direction must remain clear:

```text
infrastructure -> application -> domain
```

Domain must not depend on Spring, web, persistence, or security frameworks.

## Current Assessment Profile

If the repository context does not override this, the current technical assessment is:

- RESTful Product CRUD.
- Producto fields: id, nombre, descripcion, precio.
- Required endpoints:
  - `GET /productos`
  - `GET /productos/{id}`
  - `POST /productos`
  - `PUT /productos/{id}`
  - `DELETE /productos/{id}`
- H2 in-memory database.
- Spring Security protecting all product endpoints.
- Functional interface `CalculadorDeImpuestos`.
- `CalculadorDeImpuestosIVA` with 21% rate.
- `CalculadorDeImpuestosITBIS` with 18% rate.
- Active tax calculator selected from application properties.
- Execution-time logging aspect.
- Optional dynamic query endpoint only after mandatory scope is complete.

## Phase-Specific Guidance

### API Contract Phase

Allowed:

- OpenAPI YAML contract;
- request/response schema definitions;
- error response schema;
- security scheme in the contract.

Forbidden unless approved:

- OpenAPI code generation;
- generated DTOs;
- controller implementation;
- persistence;
- security configuration.

### Domain and Tax Phase

Allowed:

- domain model;
- domain validation;
- tax calculation interface;
- tax calculation implementations;
- minimal configuration to select calculator if the phase plan includes it;
- unit tests.

Forbidden:

- JPA annotations in domain;
- REST controllers;
- database persistence;
- security configuration.

### Persistence and Application CRUD Phase

Allowed:

- H2 configuration;
- JPA entity;
- Spring Data repository;
- persistence adapter;
- application service/use cases;
- CRUD behavior;
- tests.

Forbidden:

- REST endpoints unless the phase plan includes them;
- security implementation unless selected;
- optional dynamic query.

### REST API Phase

Allowed:

- REST controller;
- request/response DTOs;
- DTO mapper;
- validation annotations;
- REST exception handler;
- controller/integration tests.

Forbidden:

- changing domain rules without approval;
- exposing JPA entities;
- optional dynamic query unless selected.

### Security Phase

Allowed:

- Spring Security configuration;
- HTTP Basic;
- in-memory users and roles;
- protected `/productos` endpoints;
- security tests.

Forbidden unless approved:

- token servers;
- external identity providers;
- complex authorization models;
- real secret management infrastructure.

### AOP and Delivery Phase

Allowed:

- execution-time logging aspect;
- final README;
- final cleanup;
- quality validation;
- optional coverage tooling if approved;
- optional containerization or CI if approved.

Forbidden:

- broad rewrites;
- optional features before mandatory scope passes.

## Dependency Policy

Do not add dependencies unless the selected phase requires them and the phase plan allows them.

When adding a dependency:

1. Check whether it already exists.
2. Prefer standard Spring Boot starters and managed versions.
3. Avoid specifying versions already managed by Spring Boot.
4. Explain why the dependency is necessary.
5. Keep the change minimal.

Likely dependencies for this assessment may include:

```text
spring-boot-starter-web
spring-boot-starter-validation
spring-boot-starter-data-jpa
h2
spring-boot-starter-security
spring-boot-starter-aop
spring-boot-starter-test
spring-security-test
```

Only add them in the phase where they are needed.

## Testing Expectations

Use the project test conventions. If no convention exists:

- JUnit 5;
- AssertJ if available;
- Mockito where useful;
- Spring Boot Test for integration tests;
- MockMvc for REST and security tests when appropriate;
- `@DataJpaTest` for persistence tests when appropriate.

Tests should be deterministic, readable, and behavior-focused.

Coordinate with the test engineering skill for dedicated coverage work.

## Forbidden Behaviors

The agent must not:

- implement all phases at once;
- implement optional features early;
- add unapproved dependencies;
- replace existing architecture without approval;
- modify `.aicontext` decisions without asking;
- hide or delete failing tests;
- remove tests to make the build pass;
- bypass validation;
- introduce proprietary or unavailable frameworks;
- rely on external platform integrations not present in the repository;
- create external tracker items;
- include company-specific delivery conventions that do not apply to this repository.

## Final Report Format

After implementation, respond with:

```text
1. Selected phase/task
2. Tasks completed
3. Files created
4. Files modified
5. Dependencies added or changed
6. Tests added or updated
7. Maven command executed
8. Maven result
9. Task tracking updates made
10. Context updates proposed but not applied, if any
11. Out-of-scope items intentionally not implemented
12. Assumptions made
13. Risks or follow-up items
14. Suggested commit message
15. Recommended next phase/task
```

## Success Criteria

The skill succeeds when:

- only the selected phase/task is implemented;
- code compiles;
- tests pass;
- meaningful tests are added or updated;
- task tracking reflects completed work;
- no unapproved context or architecture decisions are changed;
- the implementation remains simple, readable, and defendable.

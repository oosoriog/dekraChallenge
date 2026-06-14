---
name: testit
description: Java/Spring Boot testing skill for technical assessments. Generates, reviews, fixes and reports tests phase by phase, using the project AI context and targeting at least 80% line and branch/condition coverage.
agent: testit
argument-hint: "[help|plan|review|generate|run|coverage|fix-coverage|mothers|parameterized|report|all] [class/package/phase/module] [--interactive|--automatic]"
---

# testit

## Purpose

Use this skill to design, generate, review, execute and improve tests for a Java/Spring Boot technical assessment project.

The skill focuses on meaningful tests, maintainable test code and measurable quality. The final project should reach at least:

- 80% line coverage;
- 80% branch/condition coverage where supported by the configured coverage tool.

Coverage must come from valuable tests, not superficial assertions.

## Core Rule

The project AI context is the source of truth.

Before doing any work, read the project context:

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

If these files are not available, continue only if the user explicitly allows it. Otherwise, stop and report that the AI context is missing.

If the AI context conflicts with this skill, the AI context wins.

## Context Update Policy

This skill may identify that the AI context should be updated, for example:

- a phase test task is complete;
- a coverage target should be clarified;
- a testing rule is missing;
- a test-related decision has changed.

However, do not update `.aicontext/` automatically unless the user explicitly requested tracking updates in the current task.

Default behavior:

1. Propose the context update.
2. Explain the reason.
3. Ask the user for approval before modifying `.aicontext/`.

If approval is given, update only the minimal required context files.

## When to Use

Use this skill when the user asks to:

- plan tests for a phase;
- generate unit tests;
- generate integration tests;
- review existing tests;
- improve coverage;
- fix failing tests;
- add test data builders or Object Mothers;
- refactor duplicated tests into parameterized tests;
- validate Maven test execution;
- produce a final testing report.

## When Not to Use

Do not use this skill to:

- implement new production features;
- change business behavior to make tests pass;
- bypass validation rules;
- delete failing tests instead of fixing the cause;
- inflate coverage with meaningless tests;
- introduce unnecessary test frameworks;
- modify build configuration without explicit need and explanation;
- update the project AI context without user approval.

## Workflow Commands

Parse the user's input and choose the workflow scope.

### Help

- `help`: show available commands, workflow steps, examples and target interpretation rules.

### Execution Mode

- no flag: automatic mode, execute the requested test workflow without asking between every step;
- `--interactive`: propose changes and wait for user approval before creating or modifying tests;
- `--automatic` or `--auto`: explicit automatic mode.

### Step Commands

- `plan`: create or refine a test plan for a phase, class, package or module;
- `review` or `analyze`: inspect code and existing tests, then report gaps;
- `generate` or `create`: generate or improve tests for the target;
- `run` or `run-tests`: run the relevant Maven test command;
- `coverage` or `verify`: analyze coverage and identify gaps against the 80% line and branch/condition targets;
- `fix-coverage`: add or improve meaningful tests to close coverage gaps;
- `mothers` or `create-mothers`: create reusable test data helpers, Object Mothers or builders;
- `parameterized`: refactor duplicated tests into parameterized tests;
- `report`: produce a final test report;
- `all` or empty task: execute the full workflow from planning to final report.

## Target Interpretation

- If the input contains a Java class name, target that class.
- If the input contains a package name, target that package.
- If the input contains a phase name or number, target that phase from `.aicontext/phases/` and `.aicontext/deliverables/task-backlog.md`.
- If the input contains a module name, target that module.
- If no target is specified, infer the current phase from `.aicontext/manifest.yaml` or ask the user to clarify.

## Standard Workflow

Always start by showing a short todo list for the selected workflow.

### 1. Context and Phase Check

- Read the AI context.
- Identify the current phase and allowed scope.
- Identify which tests are expected for this phase.
- Confirm whether production code may be touched. Default: no production behavior changes.
- Confirm whether `.aicontext/` may be updated. Default: propose first, update only after approval.

### 2. Build and Existing Test Baseline

Run the relevant Maven command when possible:

```bash
mvn clean test
```

If the project has separate verification or coverage profiles, use them only if already documented or configured.

Do not add coverage tooling automatically unless the current task explicitly allows it or the phase plan requires it.

### 3. Test Gap Analysis

Analyze:

- target production code;
- existing tests;
- phase tasks and acceptance criteria;
- branch and condition paths;
- error handling paths;
- validation paths;
- security paths if security is in scope;
- persistence and integration paths if persistence is in scope.

Classify gaps as:

- missing mandatory test;
- weak assertion;
- missing negative path;
- missing boundary case;
- missing branch/condition path;
- duplicated or hard-to-maintain test;
- flaky or environment-dependent test.

### 4. Test Proposal

Before writing tests in interactive mode, present:

- tests to add;
- files to create or modify;
- production code changes required, if any;
- expected coverage improvement;
- risks;
- assumptions.

In automatic mode, proceed directly if the scope is clear and safe.

### 5. Test Generation or Improvement

Create or improve tests following the project architecture.

Prefer:

- domain unit tests without Spring context;
- application service tests with mocked ports when useful;
- adapter tests for REST, persistence and security;
- integration tests only when they add real value;
- deterministic tests;
- explicit assertions;
- readable test names;
- AAA pattern: Arrange, Act, Assert.

Avoid:

- over-mocking domain logic;
- starting a Spring context for simple domain tests;
- testing framework internals;
- asserting implementation details that make refactoring hard;
- creating one huge test class for unrelated behavior;
- using random data without fixed seeds;
- relying on test execution order.

### 6. Test Data Helpers

When test setup becomes repetitive, create reusable helpers.

Allowed patterns:

- Object Mother;
- Test Data Builder;
- factory methods inside test classes for small cases.

Keep helpers test-scoped under `src/test/java`.

### 7. Parameterized Tests

Use parameterized tests when several cases share the same behavior shape.

Good candidates:

- validation invalid inputs;
- boundary cases;
- role/security matrices;
- tax calculation examples;
- filter combinations when query support exists.

Do not use parameterized tests if they reduce readability.

### 8. Test Execution

Run:

```bash
mvn clean test
```

If coverage tooling is configured, also run the documented coverage command.

If tests fail:

- inspect the failure;
- fix the root cause;
- do not remove assertions to pass tests;
- do not weaken tests without justification.

### 9. Coverage Verification

Target:

- line coverage >= 80%;
- branch/condition coverage >= 80% when available.

If coverage is below target:

1. Identify uncovered meaningful code.
2. Prioritize business logic, validations, branches and error paths.
3. Add valuable tests.
4. Re-run tests and coverage.

Coverage exclusions may be proposed for:

- generated code;
- trivial configuration classes;
- application bootstrap class;
- simple DTOs without behavior;
- code that is impossible or not valuable to test directly.

Do not add exclusions automatically unless explicitly approved.

### 10. Report and Tracking

Provide:

- files modified;
- tests added or changed;
- commands run;
- test result;
- coverage result if available;
- remaining gaps;
- proposed `.aicontext/` task status updates, if any;
- suggested commit message.

If the user approved updating `.aicontext/`, mark completed test tasks in the relevant phase or backlog document.

## Java Testing Guidelines

### Domain Tests

Use plain JUnit tests.

Good targets:

- entity/value object invariants;
- tax calculation;
- validation behavior;
- domain exceptions;
- boundary values.

Do not load Spring context for domain tests.

### Application Tests

Use unit tests with mocks/fakes for output ports.

Good targets:

- create product;
- update product;
- get product by id;
- list products;
- delete product;
- not found behavior;
- duplicate or invalid state if applicable;
- mapping between domain and ports when meaningful.

### REST Adapter Tests

Use MockMvc or the project-standard Spring MVC testing approach.

Good targets:

- status codes;
- request validation;
- response body;
- error responses;
- authentication required;
- authorization behavior;
- endpoint paths required by the assessment.

### Persistence Tests

Use the configured in-memory database for this assessment.

Good targets:

- save product;
- find by id;
- list all;
- update;
- delete;
- persistence mapping;
- decimal precision for price.

### Security Tests

When security is in scope, test at least:

- unauthenticated request is rejected;
- authenticated authorized request succeeds;
- insufficient role is rejected if roles are used;
- all required endpoints are protected.

### AOP Tests

If practical, verify that the execution-time aspect is wired and does not break application calls.

Avoid brittle tests that assert exact log timing values.

## Coverage Policy

Final target:

```text
line coverage >= 80%
branch/condition coverage >= 80%
```

Priority order for coverage work:

1. domain rules;
2. application use cases;
3. validations and error paths;
4. REST adapter behavior;
5. security behavior;
6. persistence behavior;
7. AOP wiring where practical.

Do not prioritize coverage of:

- getters/setters only;
- generated code;
- trivial bootstrap code;
- framework configuration that is better verified through integration tests.

## Build Tooling Policy

If coverage tooling is missing and the task is explicitly about coverage enforcement, propose adding a Maven-compatible coverage tool.

Default recommendation:

- JaCoCo Maven Plugin.

Before modifying `pom.xml`, report:

- why the change is needed;
- exact plugin/dependency to add;
- how it will be executed;
- whether it should enforce thresholds or only report coverage.

Do not modify `pom.xml` without explicit user approval unless the current phase plan explicitly authorizes it.

## Forbidden Behavior

Never:

- change production behavior only to make tests easier;
- hide failing tests;
- delete tests without explanation;
- replace meaningful assertions with weak assertions;
- mock the class under test;
- add broad `throws Exception` everywhere without reason;
- use sleeps or timing-sensitive tests unless unavoidable;
- introduce new test frameworks without approval;
- update `.aicontext/` without approval.

## Final Response Format

Use this format:

```text
1. Workflow executed
2. Target
3. Phase and scope validation
4. Files created/modified
5. Tests added/updated
6. Production code changes, if any
7. Commands run
8. Test result
9. Coverage result, if available
10. Remaining gaps
11. Proposed AI context updates, if any
12. Suggested commit message
```

## Success Criteria

The skill succeeds when:

- tests are meaningful and maintainable;
- Maven tests pass;
- phase acceptance criteria are covered;
- coverage is measured or planned;
- coverage gaps are addressed with valuable tests;
- no future phase is implemented accidentally;
- `.aicontext/` is respected and updated only with approval.

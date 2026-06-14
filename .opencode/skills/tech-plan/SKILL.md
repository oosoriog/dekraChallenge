---
name: tech-plan
description: Create a detailed technical implementation plan from an approved project plan, breaking phases into tasks, TODOs, files, tests, acceptance criteria, and validation commands.
agent: agent
argument-hint: "[phase|all] [--interactive|--automatic]"
---

## Purpose

Use this skill to transform an approved initial project plan into an implementation-ready technical plan.

This skill is intended for Java and Spring Boot technical assessment projects, independent backend projects, and phase-based AI-assisted development.

It does not implement code. It creates or refines detailed technical planning documents: phase specifications, task breakdowns, TODO lists, validation commands, acceptance criteria, risks, and implementation guardrails.

## Core Rule

The repository AI context is the source of truth.

Before doing anything, read the available AI context files, especially:

```text
AI_CONTEXT.md
.aicontext/manifest.yaml
.aicontext/AGENTS_EXTRA.md
.aicontext/rules/*.md
.aicontext/memory/project-decisions.md
.aicontext/deliverables/*.md
.aicontext/phases/*.md
```

If the requested technical plan conflicts with the existing AI context, do not silently override it. Report the conflict and ask the user for approval before changing project decisions or updating the project context.

## Relation to the Research/Plan Skill

This skill should normally run after a research and initial planning pass has produced:

```text
.aicontext/deliverables/analysis-and-refinement.md
.aicontext/deliverables/implementation-plan.md
.aicontext/deliverables/task-backlog.md
.aicontext/phases/*.md
```

The research/plan skill defines the high-level scope and phase model.

This skill deepens that plan into technical execution details.

## When to Use

Use this skill when:

- a project or feature has already been analyzed at a high level;
- phases exist but need technical depth;
- an implementation phase must be made ready for an implementation agent;
- the user wants a task-by-task technical plan before coding;
- TODO lists must be created or refined;
- file/package/class/test expectations must be made explicit;
- risks, dependencies, validation commands, and acceptance criteria must be specified.

Good examples:

```text
Create the technical plan for all phases of this assessment.
```

```text
Create a detailed technical plan for Phase 1 only.
```

```text
Expand the task backlog into implementation-ready TODOs for the Producto CRUD project.
```

## When Not to Use

Do not use this skill to:

- implement production code;
- implement tests;
- modify Java source files;
- modify Java test files;
- modify runtime configuration;
- modify build files unless the task is explicitly to plan the required build changes;
- add dependencies;
- create controllers, services, repositories, entities, DTOs, migrations, security configuration, aspects, or CI files;
- execute a phase;
- mark implementation TODOs as done;
- make project decisions that contradict the AI context without user approval;
- create items in external trackers;
- rely on unavailable tool integrations;
- use proprietary or company-specific frameworks, libraries, templates, certification flows, or conventions.

## Inputs Expected From the User

The user should provide one or more of:

- selected phase to plan;
- request to plan all phases;
- assessment statement or feature description;
- initial research/plan output;
- current repository state;
- technical constraints;
- known decisions;
- forbidden technologies;
- desired validation criteria;
- approval to update AI context planning files.

If the user asks for a technical plan but does not specify a phase, inspect the existing planning files and choose the next logical phase to plan. If multiple choices are reasonable, ask one concise question or proceed with an explicit assumption.

## Repository Review

Before writing a technical plan, inspect the repository enough to avoid unrealistic instructions.

Look for:

- base package name;
- Java version and Maven compiler configuration;
- Spring Boot version;
- existing dependencies;
- existing package structure;
- existing test libraries;
- existing application configuration;
- existing OpenAPI files;
- existing persistence setup;
- existing security setup;
- existing AOP/logging setup;
- existing AI context and phase files.

Do not modify repository implementation files during this skill.

## External Research Policy

Use external research only when needed to make the technical plan correct and current.

Good reasons to research:

- official Spring Boot compatibility;
- official Spring Security configuration patterns;
- official H2 or Spring Data JPA behavior;
- OpenAPI specification details;
- Maven plugin compatibility;
- Java release compatibility;
- testing best practices for the selected framework version.

Research must prioritize authoritative sources: official documentation, specifications, release notes, and stable project docs.

Do not use research to expand scope, add unnecessary tools, or invent business requirements.

When research affects the plan, document:

- what was checked;
- why it matters;
- decision or recommendation;
- any remaining uncertainty.

## Question Policy

Ask the user before finalizing the technical plan when a choice materially affects implementation.

Examples:

- adding a new dependency;
- changing Java target or compiler settings;
- choosing manual OpenAPI implementation versus code generation;
- choosing authentication approach;
- implementing optional scope;
- changing package structure significantly;
- replacing an existing project decision;
- updating AI context decisions.

If a question is not blocking, continue with a clearly marked assumption.

## AI Context Update Policy

This skill may propose or apply updates to `.aicontext/`, but must not change project decisions silently.

Before updating AI context files, do one of the following:

1. If the user explicitly asked to update planning docs, proceed and summarize the changes.
2. If the update changes or replaces an existing decision, ask for user approval first.
3. If the update only expands already approved phase/task details, proceed and clearly report the files modified.

Allowed documentation targets, when approved or explicitly requested:

```text
.aicontext/deliverables/technical-plan.md
.aicontext/deliverables/task-backlog.md
.aicontext/deliverables/implementation-plan.md
.aicontext/phases/*.md
.aicontext/todos/*.md
.aicontext/memory/project-decisions.md
```

Do not modify source code, tests, build files, runtime configuration, or CI/CD files during this skill.

## Planning Modes

### Mode A - Full Technical Planning

Use when the user asks to plan the whole project.

Output:

- one technical overview;
- one detailed technical section per phase;
- implementation-ready task backlog;
- TODO lists;
- dependencies and sequencing;
- validation gates;
- risks and open questions;
- recommended first implementation phase.

Depth:

- enough for an implementation agent to start Phase 1 safely;
- later phases may be detailed, but should be revisited before implementation if earlier phases change assumptions.

### Mode B - Selected Phase Technical Planning

Use when the user asks to plan one phase.

Output:

- very detailed technical plan for the selected phase;
- concrete files/packages/classes expected;
- tests to write;
- dependencies if any;
- exact validation commands;
- acceptance criteria;
- TODO checklist;
- stop condition.

Depth:

- implementation-ready.

### Mode C - Backlog Refinement

Use when the phase files exist but tasks are too vague.

Output:

- task IDs;
- task descriptions;
- status markers;
- dependencies between tasks;
- acceptance criteria per task;
- traceability to requirements;
- test mapping.

## Technical Planning Depth

A technical plan should include enough detail to guide implementation without writing the implementation itself.

Include:

- package layout;
- classes/interfaces to create;
- responsibilities per class;
- method signatures when helpful;
- DTO/schema names;
- validation strategy;
- persistence strategy;
- error handling strategy;
- security strategy;
- test categories;
- test case names or scenarios;
- dependencies to add, if approved or required;
- Maven commands;
- acceptance criteria;
- stop condition;
- commit suggestion.

Do not include:

- full production code;
- full test code;
- generated code;
- large snippets that should be created by the implementation skill;
- speculative optional features;
- enterprise patterns that do not help the assessment.

## Java and Spring Boot Planning Principles

Prefer:

- Java version compatibility stated explicitly;
- Maven configuration kept minimal;
- Spring Boot standard conventions;
- simple package structure;
- pragmatic hexagonal architecture if the AI context requires it;
- domain independent from Spring and persistence where useful;
- DTOs kept outside the domain;
- JPA entities kept outside the domain when using hexagonal architecture;
- BigDecimal for monetary values;
- explicit validation rules;
- explicit error responses;
- simple security when the assessment does not require advanced auth;
- meaningful tests from the start;
- small implementation phases;
- reviewable commits.

Avoid:

- unnecessary framework abstractions;
- unnecessary custom infrastructure;
- premature generic libraries;
- optional features before mandatory requirements;
- complex security unless required;
- code generation unless explicitly approved;
- database choices that contradict the assessment;
- superficial tests.

## Recommended Workflow

### Step 1 - Read AI Context

Read the AI context and summarize:

- active project purpose;
- approved decisions;
- active phase;
- planned phases;
- forbidden actions;
- existing phase/task files;
- conflicts or outdated decisions.

### Step 2 - Inspect Repository

Inspect the repository enough to ground the technical plan.

Summarize:

- build tool;
- Java/Spring version;
- dependency state;
- existing code structure;
- existing tests;
- configuration files;
- missing prerequisites.

### Step 3 - Select Planning Scope

Determine whether the user asked for:

- full technical plan;
- selected phase technical plan;
- backlog refinement;
- validation of an existing plan.

If unclear, choose the safest useful scope and state the assumption.

### Step 4 - Resolve Blocking Questions

Ask the user if there are blocking decisions.

Do not ask about details that can be safely planned with assumptions.

### Step 5 - Produce Technical Plan

Generate the technical plan using the templates in `assets/`.

For each phase or task, define:

- task ID;
- objective;
- implementation notes;
- files likely to change;
- tests;
- validation command;
- acceptance criteria;
- out of scope;
- stop condition.

### Step 6 - Update Planning Files

If allowed, update:

```text
.aicontext/deliverables/technical-plan.md
.aicontext/deliverables/task-backlog.md
.aicontext/phases/*.md
.aicontext/todos/*.md
```

If not allowed, provide the plan in the response and ask for approval to write it.

### Step 7 - Final Report

End with a concise report and recommended next action.

## Output File Structure

When documentation updates are approved, prefer:

```text
.aicontext/deliverables/technical-plan.md
.aicontext/deliverables/task-backlog.md
.aicontext/phases/00-analysis-and-planning.md
.aicontext/phases/01-api-contract.md
.aicontext/phases/02-domain-and-tax-calculation.md
.aicontext/phases/03-persistence-and-application-crud.md
.aicontext/phases/04-rest-api-and-error-handling.md
.aicontext/phases/05-security.md
.aicontext/phases/06-aop-quality-and-delivery.md
.aicontext/phases/07-optional-dynamic-query.md
.aicontext/todos/README.md
.aicontext/todos/phase-01-api-contract.md
.aicontext/todos/phase-02-domain-and-tax-calculation.md
...
```

Use the names already present in the repository if they differ.

## Task Status Format

Use this status format for TODOs:

```md
- [ ] TASK-001 - Short task title
  - Phase: Phase N - Name
  - Requirement trace: REQ-XXX
  - Description: What must be done
  - Files: Expected files or packages
  - Tests: Expected tests
  - Acceptance criteria:
    - [ ] Criterion 1
    - [ ] Criterion 2
  - Notes: Important constraints or assumptions
```

Implementation skills may later change `[ ]` to `[x]` only after the task is actually completed and validated.

## Suggested Technical Plan Template

Use `assets/tech-plan-template.md` as the preferred structure for `.aicontext/deliverables/technical-plan.md`.

## Suggested Phase Detail Template

Use `assets/phase-detail-template.md` as the preferred structure for `.aicontext/phases/*.md`.

## Suggested TODO Backlog Template

Use `assets/todo-backlog-template.md` as the preferred structure for `.aicontext/todos/*.md` or `.aicontext/deliverables/task-backlog.md`.

## Final Response Format

Use this format when the skill completes:

```text
1. Context and repository reviewed
   - AI context files reviewed
   - Repository facts used

2. Planning scope
   - Full project / selected phase / backlog refinement
   - Assumptions made

3. Questions or approvals needed
   - Blocking questions
   - Decisions requiring approval
   - Non-blocking assumptions

4. Technical plan summary
   - Phase list
   - Key technical decisions
   - Dependencies or build changes planned
   - Validation strategy

5. Files created or modified
   - Documentation files only

6. Task backlog summary
   - Number of phases
   - Number of tasks
   - First task to implement

7. Safety confirmation
   - No production code modified
   - No test code modified
   - No build/runtime/CI configuration modified
   - No dependencies added

8. Next recommended step
   - Usually: run the implementation-by-phase skill for the first planned phase
```

## Success Criteria

This skill succeeds when:

- the technical plan is grounded in the repository and AI context;
- conflicts with context are surfaced;
- user approval is requested for decision changes;
- each phase has implementation-ready tasks;
- tests and validation are planned;
- TODOs are traceable and actionable;
- optional work is clearly separated;
- no implementation work has been performed.

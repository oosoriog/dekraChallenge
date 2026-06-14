---
name: research-plan
description: Analyze a project or technical assessment, review existing AI context, identify open questions, and create an initial phase-based plan without implementing code.
agent: agent
argument-hint: "[project/task description] [--interactive|--automatic]"
---

## Purpose

Use this skill to turn a broad project or feature description into a clear initial analysis and phase-based delivery plan.

This skill is intended for technical assessment projects and independent Java/Spring Boot backends. It combines lightweight research and functional planning. It does **not** create a detailed technical implementation specification; that belongs to a later technical-planning skill.

The output of this skill is a researched and reviewed initial plan: scope, assumptions, open questions, phases, high-level deliverables, risks, and a safe next step.

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
```

If the requested task conflicts with the existing AI context, do not silently override it. Report the conflict and ask the user for approval before updating project context or changing the plan.

## When to Use

Use this skill when the user provides a broad task such as:

- creating a complete project from a technical assessment statement;
- planning a new backend application;
- analyzing requirements before implementation;
- converting an idea into a phase-based project plan;
- reviewing the repository and deciding what needs to be planned next;
- defining initial phases before a detailed technical plan exists.

Good examples:

```text
Analyze this technical assessment and create the initial project plan.
```

```text
Review the current repository and define the phases needed to implement the requested product CRUD API.
```

```text
Create the initial research and planning documents for this backend project.
```

## When Not to Use

Do not use this skill to:

- implement production code;
- implement tests;
- modify build configuration;
- add dependencies;
- create controllers, services, repositories, entities, DTOs, migrations, security configuration, or runtime configuration;
- create detailed class-by-class implementation instructions;
- perform a full low-level technical design for every task;
- execute a complete implementation phase;
- make architectural decisions that contradict the AI context without asking the user;
- create items in external trackers;
- rely on unavailable tool integrations;
- use proprietary or company-specific frameworks, libraries, naming conventions, certification flows, or internal templates.

## Inputs Expected From the User

The user should provide one or more of:

- the full task or assessment statement;
- business requirements;
- technical constraints;
- desired stack;
- repository location or current repository state;
- known decisions;
- forbidden technologies or constraints;
- evaluation criteria.

If essential information is missing, ask targeted questions before creating a final plan. If questions are not blocking, continue with clearly marked assumptions.

## Repository Review

Before planning, inspect the repository at a high level.

Look for:

- project type and language;
- build tool;
- framework version;
- current package structure;
- existing dependencies;
- existing tests;
- current configuration files;
- existing documentation;
- existing AI context;
- current phase or planning files;
- contradictions between the repository and the requested task.

Do not make code changes during this skill.

## External Research Policy

Use external research only when needed to answer planning-relevant questions, such as:

- current framework compatibility;
- official documentation for a library or standard;
- version constraints;
- security recommendations;
- build or runtime compatibility;
- whether a proposed approach is realistic.

Research must prioritize authoritative sources such as official documentation, specifications, and stable project documentation.

Do not use external research to invent business requirements. Do not turn a simple assessment into an enterprise design because external sources mention advanced patterns.

When research affects a decision, document:

- what was checked;
- why it matters;
- the decision or recommendation;
- any remaining uncertainty.

## Question Policy

Ask the user before finalizing the plan when any of these are unclear or contradictory:

- business scope;
- mandatory versus optional requirements;
- Java or framework version;
- database choice;
- security approach;
- whether an optional feature should be implemented;
- whether a dependency can be added;
- whether the AI context should be updated;
- whether an existing decision should be replaced.

Use concise questions. Group related questions together.

If the user has already provided enough information, do not ask unnecessary questions. Mark minor unknowns as `[TBD]` or assumptions.

## AI Context Update Policy

This skill may propose updates to `.aicontext/`, but must not change project decisions silently.

Before updating AI context files, do one of the following:

1. If the user explicitly asked to update the AI context, proceed and summarize the changes.
2. If the update changes or replaces an existing decision, ask for approval first.
3. If the update only fills planning details already approved by the user, proceed and clearly report the files modified.

Allowed documentation targets, when approved or explicitly requested:

```text
.aicontext/deliverables/analysis-and-refinement.md
.aicontext/deliverables/implementation-plan.md
.aicontext/deliverables/task-backlog.md
.aicontext/deliverables/architecture-summary.md
.aicontext/deliverables/interview-defense.md
.aicontext/memory/project-decisions.md
.aicontext/phases/*.md
```

Do not modify source code, tests, build files, runtime configuration, or CI/CD files during this skill.

## Planning Depth

This skill creates an initial plan, not a detailed implementation specification.

Include:

- high-level phases;
- goals per phase;
- expected deliverables per phase;
- mandatory versus optional scope;
- main risks;
- open questions;
- high-level validation strategy;
- recommended next phase;
- where deeper technical planning is required.

Do not include:

- exact class implementations;
- method-by-method design;
- exact mapper code;
- exact SQL or schema unless the assessment already requires it;
- full controller implementation details;
- complete test code;
- low-level build plugin configuration unless it is a planning blocker.

## Recommended Workflow

### Step 1 — Read Context

Read the existing AI context and summarize the active constraints.

Identify:

- current project purpose;
- active phase;
- already approved decisions;
- forbidden actions;
- known stack;
- existing plan files.

### Step 2 — Understand the Task

Analyze the user-provided task or assessment statement.

Extract:

- business domain;
- actors or users;
- functional requirements;
- non-functional requirements;
- mandatory scope;
- optional scope;
- constraints;
- evaluation criteria;
- explicit technologies;
- contradictions with existing context.

### Step 3 — Review the Repository

Inspect the repository at a high level and compare it with the requested task.

Identify:

- what already exists;
- what is missing;
- what must be changed later;
- whether the base project compiles, if this can be checked safely;
- whether dependency choices are already present;
- whether the current project structure helps or conflicts with the plan.

### Step 4 — Research If Necessary

Only research when needed.

Use research to validate planning-critical facts, not to expand the project scope.

### Step 5 — Ask Clarifying Questions

Ask only important questions.

Questions should focus on decisions that would materially change the plan. If the plan can continue safely with an assumption, mark it clearly.

### Step 6 — Generate Initial Plan

Produce an initial plan with phases.

For each phase, include:

- phase objective;
- main deliverables;
- mandatory scope;
- optional scope if any;
- high-level tests or validation;
- dependencies on previous phases;
- stop condition;
- whether a detailed technical plan is required before implementation.

### Step 7 — Propose AI Context Updates

If planning documents should be updated, state exactly which files should change and why.

If approval is required, ask before applying the changes.

If approval was already given, update the documentation and report the changes.

### Step 8 — Final Report

End with a concise report.

## Output Files

When documentation updates are approved, prefer this structure:

```text
.aicontext/deliverables/analysis-and-refinement.md
.aicontext/deliverables/implementation-plan.md
.aicontext/deliverables/task-backlog.md
.aicontext/phases/00-analysis-and-planning.md
.aicontext/phases/01-<phase-name>.md
.aicontext/phases/02-<phase-name>.md
...
```

The phase files created by this skill should remain high-level. A later technical-planning skill should expand them into implementation-ready tasks.

## Suggested Phase File Template

Use this structure for each initial phase file:

```md
# Phase N — Phase Name

## Objective

## Why This Phase Exists

## Scope

## Out of Scope

## Main Deliverables

## High-Level Tasks

## Validation Strategy

## Dependencies on Previous Phases

## Risks / Open Questions

## Stop Condition

## Needs Detailed Technical Plan

Yes/No. Explain briefly.
```

## Suggested Analysis Document Template

Use this structure for `analysis-and-refinement.md`:

```md
# Analysis and Refinement

## Task Summary

## Business Domain

## Actors / Users

## Functional Requirements

## Non-Functional Requirements

## Mandatory Scope

## Optional Scope

## Explicit Constraints

## Existing Repository Findings

## Existing AI Context Findings

## Decisions Confirmed

## Decisions Requiring Approval

## Assumptions

## Open Questions

## Risks

## Recommended Initial Scope

## Recommended Phase Plan

## Recommended Next Step
```

## Suggested Implementation Plan Template

Use this structure for `implementation-plan.md`:

```md
# Implementation Plan

## Planning Status

## Approved Scope

## Phase Overview

## Phase 0 — Analysis and Planning

## Phase 1 — <Name>

## Phase 2 — <Name>

## Phase 3 — <Name>

## Later / Optional Phases

## Validation Strategy

## Review Gates

## Commit Strategy

## Next Required Technical Plan
```

## General Planning Principles

Prefer:

- simple, complete, reviewable increments;
- clear phase boundaries;
- tests planned from the beginning;
- minimal dependencies;
- framework-standard approaches;
- architecture that is useful but not excessive;
- decisions that are easy to defend in an interview or code review.

Avoid:

- speculative features;
- optional work before mandatory work;
- excessive abstraction;
- hidden assumptions;
- large phases that are hard to review;
- changes to project context without approval;
- references to unavailable tools or organization-specific workflows.

## Final Response Format

Use this format when the skill completes:

```text
1. Context read
   - Files reviewed
   - Active constraints found

2. Repository findings
   - Current project state
   - Relevant existing dependencies/configuration
   - Gaps or contradictions

3. Requirement analysis
   - Domain
   - Mandatory requirements
   - Optional requirements
   - Non-functional requirements

4. Research performed
   - Yes/No
   - Sources or topics checked
   - Findings that affect the plan

5. Questions for the user
   - Blocking questions
   - Non-blocking questions
   - Assumptions used for now

6. Initial phase plan
   - Phase list
   - Objective of each phase
   - Deliverables of each phase
   - Stop condition of each phase

7. Proposed AI context updates
   - Files to create/update
   - Whether approval is needed

8. Next recommended step
   - Usually: run the technical-planning skill for the first implementation phase

9. Safety confirmation
   - No production code modified
   - No test code modified
   - No build/runtime/CI configuration modified
```

## Success Criteria

This skill succeeds when:

- the task is understood;
- existing AI context has been respected;
- contradictions are surfaced;
- important questions are asked;
- assumptions are explicit;
- an initial phase plan exists;
- the next technical-planning step is clear;
- no implementation work has been performed.

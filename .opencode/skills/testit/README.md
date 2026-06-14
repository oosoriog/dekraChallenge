# testit

Java/Spring Boot testing skill for technical assessment projects.

## Goal

Generate, improve, run and report tests phase by phase while respecting the project AI context.

Final testing target:

- 80% or higher line coverage;
- 80% or higher branch/condition coverage when supported by the coverage tool;
- meaningful tests over superficial coverage inflation.

## Recommended use

```text
Use the assessment-test-engineer skill.

Generate tests for Phase 2 only.
Respect .aicontext and do not update it without asking first.
Run mvn clean test and report results.
```

## Common commands

```text
assessment-test-engineer help
assessment-test-engineer plan Phase 1
assessment-test-engineer review ProductoService
assessment-test-engineer generate ProductoController --interactive
assessment-test-engineer run-tests
assessment-test-engineer coverage
assessment-test-engineer fix-coverage
assessment-test-engineer mothers Producto
assessment-test-engineer parameterized validation tests
assessment-test-engineer report
assessment-test-engineer all Phase 3
```

## Files

```text
assessment-test-engineer/
├── SKILL.md
├── README.md
└── assets/
    ├── coverage-policy.md
    ├── java-testing-patterns.md
    ├── object-mother-template.md
    ├── phase-test-plan-template.md
    ├── test-report-template.md
    └── test-workflow-template.md
```

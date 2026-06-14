# Test Workflow Template

## Input

```text
Use the assessment-test-engineer skill.

[command] [target] [mode]
```

Examples:

```text
Use the assessment-test-engineer skill.
Generate tests for Phase 2 --interactive.
```

```text
Use the assessment-test-engineer skill.
Fix coverage for ProductoApplicationService.
```

## Workflow

1. Read `.aicontext/`.
2. Identify phase and scope.
3. Run baseline tests if possible.
4. Analyze coverage/test gaps.
5. Propose test changes if interactive.
6. Generate or improve tests.
7. Run Maven tests.
8. Report result and proposed context updates.

## Stop Conditions

Stop and ask the user if:

- target cannot be resolved;
- active phase is unclear;
- fixing tests requires changing production behavior;
- `pom.xml` must be modified but approval was not given;
- `.aicontext/` should be updated but approval was not given.

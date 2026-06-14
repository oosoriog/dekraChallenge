# Phase Test Plan Template

## Phase

`[Phase number and name]`

## Scope

`[What production scope is being tested]`

## Test Types

- Unit tests:
- Slice tests:
- Integration tests:
- Security tests:
- Coverage checks:

## Required Test Cases

| ID | Scenario | Type | Expected Result | Priority |
|---|---|---|---|---|
| T-001 | [scenario] | [unit/integration] | [result] | [must/should] |

## Edge Cases

- [edge case]

## Negative Cases

- [negative case]

## Coverage Focus

- Lines:
- Branches/conditions:
- Error paths:

## Commands

```bash
mvn clean test
```

## Acceptance Criteria

- [ ] All required tests pass.
- [ ] Mandatory behavior is covered.
- [ ] No future phase is implemented.
- [ ] Coverage gaps are reported or fixed.

## Proposed Context Updates

List proposed `.aicontext/` updates here. Ask the user before applying them.

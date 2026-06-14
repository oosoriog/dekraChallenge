# AI Context Update Policy

## Source of truth

`.aicontext/` is the project contract.

## Allowed updates without asking

Only after a task succeeds:

- mark completed checkboxes in the selected phase file;
- mark completed checkboxes in `task-backlog.md`;
- add short implementation notes to the selected phase file;
- add Maven validation result to the selected phase file.

## Updates requiring user approval

Ask before changing:

- project decisions;
- architecture rules;
- phase order;
- scope;
- accepted technologies;
- dependency policy;
- security strategy;
- persistence strategy;
- API-first approach;
- optional feature activation.

## How to request approval

Use this format:

```text
I found that the current context may need an update.

Proposed context change:
- File:
- Current decision:
- Proposed decision:
- Reason:
- Impact:

Please approve before I update this context.
```

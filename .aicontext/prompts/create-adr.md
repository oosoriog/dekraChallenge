# Prompt: Create ADR (Architecture Decision Record)

Use this prompt template to record an architectural decision.

---

## Template

```
Create an Architecture Decision Record for the following decision:

TITLE: [short imperative title, e.g., "Use UUID as entity identifier"]
CONTEXT: [what situation prompted this decision]
DECISION: [what was decided]
RATIONALE: [why this option was chosen over alternatives]
ALTERNATIVES CONSIDERED:
- [alternative 1 and why it was rejected]
- [alternative 2 and why it was rejected]
CONSEQUENCES:
- Positive: [benefits]
- Negative: [trade-offs or risks]
STATUS: Accepted

Save as: .aicontext/deliverables/adr/ADR-{NNN}-{short-title}.md
Register in: .aicontext/deliverables/adr/README.md
```

---

## ADR File Structure

```markdown
# ADR-NNN — Title

**Date:** YYYY-MM-DD
**Status:** Accepted | Deprecated | Superseded by ADR-XXX
**Phase:** Phase N

## Context
...

## Decision
...

## Rationale
...

## Alternatives Considered
...

## Consequences
### Positive
...
### Negative
...
```

---

## Numbering

ADRs are numbered sequentially starting at 001.
The sequence must not be reused even if an ADR is deprecated.


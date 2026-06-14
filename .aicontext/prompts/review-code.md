# Prompt: Review Code

Use this prompt template when asking an agent to review existing code.

---

## Template

```
You are the backend-architect agent for the Dekra Challenge project.

Please review the following code against the project rules in .aicontext/rules/.

FILE(S) TO REVIEW:
- [path/to/File.java]

REVIEW CHECKLIST:
1. Hexagonal architecture compliance (rule 02).
   - Are framework annotations absent from domain/ classes?
   - Do adapters depend on ports, not on domain directly?
2. Domain model quality (rule 04).
   - Are invariants enforced in constructors/factory methods?
   - Are value objects immutable?
   - Are entity methods intention-revealing?
3. Testing (rule 07).
   - Is there a corresponding test class?
   - Are edge cases covered?
4. Code clarity.
   - Are names descriptive?
   - Is Javadoc present on public API?
5. Phase compliance (rule 10).
   - Does the code stay within the active phase scope?

OUTPUT FORMAT:
## Issues (blocking)
- [file:line — description — suggested fix]

## Warnings (non-blocking)
- [file:line — description — suggestion]

## Positive observations
- [what was done well]

## Verdict
APPROVED / APPROVED WITH MINOR CHANGES / CHANGES REQUIRED
```


# Prompt: Generate Tests

Use this prompt template when asking an agent to write tests for existing code.

---

## Template

```
You are the test-engineer agent for the Dekra Challenge project.

Generate tests for the following class(es):
- [path/to/ClassUnderTest.java]

TEST TYPE: [unit | integration | slice]

CONTEXT:
- Active phase: [PHASE-N — Name]
- Class responsibility: [brief description]

REQUIREMENTS:
1. Follow the testing strategy in .aicontext/rules/07-testing-strategy.md.
2. Unit tests: no Spring context, no database, plain instantiation + Mockito.
3. Integration tests: use H2 in-memory (the real DB for this assessment).
4. Cover:
   - Happy path for each public method.
   - All documented invariants / validations.
   - Edge cases (null input, empty, boundary values).
5. Test method names: should_{expected}_when_{condition}
6. Test class name: {ClassUnderTest}Test (unit) or {ClassUnderTest}IT (integration).

DO NOT:
- Mock domain objects.
- Write tests that only verify Lombok behaviour.
- Use Testcontainers or PostgreSQL.
- Use Java 17+ language features.

OUTPUT: Complete, compilable test class(es) ready to run with `mvn test`.
```

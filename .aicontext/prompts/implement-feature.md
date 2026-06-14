# Prompt: Implement Feature

Use this prompt template when asking an agent to implement a new feature.

---

## Template

```
You are the spring-boot-implementer agent for the Dekra Challenge project.

CONTEXT:
- Active phase: [PHASE-N — Name]
- Feature request: [brief description]
- Acceptance criteria:
  1. [criterion 1]
  2. [criterion 2]

CONSTRAINTS:
- Follow all rules in .aicontext/rules/ strictly.
- Do not implement anything outside the active phase scope.
- Do not add dependencies/plugins without approval (openapi-generator, H2, Spring Security, AOP, JaCoCo are gated to their phases).
- API-first: respect the OpenAPI contract; generated DTOs stay at the web boundary (never the domain).
- Write tests before or alongside production code.
- Respect the lightweight hexagonal architecture (see rule 02); do not over-engineer.
- Use Java 17 (no newer features/APIs). Use BigDecimal for money (setScale(2, HALF_UP) for tax/final price).
- Soft delete + audit fields are mandatory (see rule 06); deletedBy comes from the authenticated principal.
- Do not modify pom.xml without approval.
- Verify Spring Boot 4 compatibility of any pattern used.

TASK:
1. Design the domain/application/adapter classes required.
2. List files to be created or modified (get approval if unsure).
3. Implement with meaningful tests.
4. Verify with: ./mvnw verify
5. Report using the standard Done/Decisions/Tests/Next format.
```

---

## Example Usage

> The domain is known (Producto). Names below are concrete to this assessment.

```
Active phase: Phase 2 — Domain Model & Tax Calculation
Feature request: Create the Producto domain type and the tax calculators.
Acceptance criteria:
  1. Producto requires nombre (not blank) and precio (BigDecimal, >= 0).
  2. CalculadorDeImpuestosIVA applies 21%; CalculadorDeImpuestosITBIS applies 18% (setScale(2, HALF_UP)).
  3. The active calculator is selected via app.tax.type (IVA | ITBIS).
```


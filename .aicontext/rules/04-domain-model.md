# Rule 04 — Domain Model (Product + Tax Calculation)

## Principle

The domain model captures the business concepts in plain Java. It must be
**independently testable** without starting Spring or a database, and must remain
independent from OpenAPI-generated DTOs.

> **Naming:** Internal code (domain, application, persistence, web mapper) is written in
> **English**. The public API contract — the `/productos` paths and the generated DTOs
> (`ProductoRequest`/`ProductoResponse`) with their Spanish JSON field names — is intentionally
> preserved in Spanish per the challenge statement; the web mapper translates between the two.

> **Phase gate:** Domain modelling is the focus of **Phase 2 — Domain Model & Tax Calculation**.

---

## The Domain (concrete)

### `Product`
- Fields: `id` (`Long`, generated), `name` (`String`), `description` (`String`),
  `price` (`BigDecimal`, the **base** price).
- Invariants: `name` not blank; `price` not null and `>= 0`.
- Money is **always** `BigDecimal` — never `double`/`float`.
- The domain `Product` is **hand-written** and free of JPA, Spring and generated-DTO types.

> Audit/soft-delete fields (`deleted`, `createdAt`, `updatedAt`, `deletedAt`, `deletedBy`)
> are **persistence concerns** (see rule 06) on the JPA entity — not domain invariants.
> Keep them out of the pure domain model unless the tech plan justifies a minimal domain flag.

### `TaxCalculator` (functional interface)
```java
@FunctionalInterface
public interface TaxCalculator {
    BigDecimal calculate(BigDecimal price);
}
```

### Implementations
- `IvaTaxCalculator` — rate `0.21` (21%).
- `ItbisTaxCalculator` — rate `0.18` (18%).
- Tax = `price.multiply(rate)`, using **`setScale(2, HALF_UP)`** unless the tech plan
  finds a better justified approach. Final price (`price + tax`) also uses scale 2, HALF_UP.

### Selection
- A `TaxType` enum (`IVA`, `ITBIS`) maps the `app.tax.type` property.
- Selection of the active calculator is wired in `config/` (Spring), keeping the domain
  framework-free. Exactly one calculator is active at a time.

---

## Tax Exposure (computed, not stored)

Only the **base price** is persisted. Tax values are **computed on read** and exposed in the
generated `ProductoResponse` DTO (see rule 03). The response field names stay Spanish (public
contract); the domain side is English.

| Response field (DTO, Spanish) | Source (domain, English) |
|---|---|
| `precio` | Base price stored in H2 (`Product.getPrice()`) |
| `tipoImpuesto` | Active `TaxType` from `app.tax.type` (IVA \| ITBIS) |
| `porcentajeImpuesto` | Percentage of the active calculator (e.g. 21, 18) |
| `importeImpuesto` | `taxCalculator.calculate(price)` — scale 2, HALF_UP |
| `precioConImpuesto` | `price + importeImpuesto` — scale 2, HALF_UP |

Rules:
- Do not expose multiple taxes at once; do not accept a tax choice per request.
- The tax computation belongs to the domain calculators; the web adapter maps the result
  into the response DTO. Do not scatter tax math into controllers.

---

## Allowed Constructs (Java 17)

| Construct | Usage |
|---|---|
| Java `class` | `Product` model |
| Java `record` | Small immutable value carriers (e.g. `ProductFilter`, `CalculatedTax`) |
| Java `enum` | `TaxType` and any finite vocabulary |
| `@FunctionalInterface` | `TaxCalculator` |
| Lombok | Optional boilerplate reduction |

> Do **not** use Java 18–21-only language features or APIs (target is Java 17).

---

## Rules

- Keep the domain free of Spring, JPA and generated-DTO imports (tax + validation logic especially).
- Enforce invariants in constructors/factory methods; reject invalid state early.
- Use a clear domain exception for not-found (`ProductNotFoundException`).
- Do not over-model: no typed-ID value objects, no aggregates beyond `Product` —
  this is a single-entity CRUD.

---

## What to Avoid

- `double`/`float` for `price` or tax.
- Framework/generated-DTO imports inside the domain (tax logic must be plain Java).
- Storing computed tax or final price (persist only the base `price`).
- Anemic-but-scattered logic: tax math belongs in the calculators, not in controllers.
- Inventing extra entities, statuses or relationships not in the statement.

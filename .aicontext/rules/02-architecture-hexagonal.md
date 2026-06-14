# Rule 02 — Hexagonal Architecture (Ports & Adapters)

## Principle

The domain model and application logic must be **completely isolated** from infrastructure.
Framework annotations, JPA annotations, Spring beans and HTTP concerns must **never appear**
in the `domain/` package.

---

## Layer Responsibilities

### Domain (Inner Ring)

- Contains: entities, value objects, aggregates, domain events, domain services, repository ports.
- Rules:
  - **No Spring annotations.**
  - **No JPA/Hibernate annotations.**
  - **No framework dependencies** of any kind.
  - Pure Java only.
  - All business invariants are enforced here.

### Application (Use Cases)

- Contains: the application service (`ProductService`) orchestrating domain + repository port.
- Rules:
  - May use Spring `@Service`.
  - Orchestrates the domain; holds no business rules itself (those live in the domain).
  - Depends on the **output port** (`ProductRepository`).
  - For a simple CRUD, a single service is enough — do **not** create one input-port
    interface per operation unless it adds real value.

### Adapters — Primary (Inbound)

- Package: `adapter/in/web/`
- Contains: Spring MVC `@RestController` classes, request/response DTOs, mappers.
- Rules:
  - Depend on the application service (`ProductService`), not on persistence.
  - DTOs stay within this layer; JPA entities must not leave the persistence adapter.

### Adapters — Secondary (Outbound)

- Package: `adapter/out/persistence/`
- Contains: Spring Data JPA repository interfaces, JPA entity classes, mapper classes.
- Rules:
  - Must implement **output ports** defined in the domain/application layer.
  - JPA entities are infrastructure details; map them to/from domain objects explicitly.
  - The domain must never see a JPA entity.

### Configuration

- Package: `config/`
- Contains: `@Configuration` classes, `@Bean` definitions, `TaxConfig` (calculator
  selection from `app.tax.type`), Spring Security config (phase 5).

---

## Dependency Rule

```
Adapters → Application → Domain
               ↑
           (ports)
               ↑
          Adapters (out)
```

Dependencies always point **inward**. The domain depends on nothing outside itself.

---

## Naming Convention (concrete domain: Product)

> Internal types are **English**. The public REST path (`/productos`) and the generated DTO
> names (`ProductoRequest`/`ProductoResponse`) stay Spanish as part of the API contract.

| Type | Name |
|---|---|
| Domain model | `Product` |
| Functional interface | `TaxCalculator` |
| Output port (repository interface) | `ProductRepository` (domain package) |
| Primary adapter | `ProductController` (implements generated `ProductosApi`) |
| Secondary adapter | `ProductRepositoryAdapter` |
| Spring Data repo | `SpringDataProductRepository` |
| JPA entity | `ProductJpaEntity` (table `product`) |

> Keep it pragmatic: do not create interfaces that add no value. A single repository
> port is enough; do not invent per-operation use-case interfaces for a simple CRUD.

---

## Anti-Patterns to Avoid

- `@Entity` on a domain class.
- Autowiring a `JpaRepository` directly into a use-case.
- Returning JPA entities from a REST endpoint.
- Putting business logic in a `@RestController`.
- God services that implement multiple unrelated use cases.


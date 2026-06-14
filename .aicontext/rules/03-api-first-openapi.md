# Rule 03 — API-First & OpenAPI Code Generation

## Principle

This project is **API-first**. The OpenAPI contract is authored and reviewed **before**
the REST controllers are implemented. The contract is the source of truth for the HTTP API.

> **Phase gate:** The contract is produced in **Phase 1 — API Contract & OpenAPI Codegen**,
> before any controller logic exists.

---

## Contract Location

```
src/main/resources/openapi/productos-api.yaml
```

OpenAPI 3 (3.0.x or 3.1.x — confirm generator compatibility in the tech plan).

---

## OpenAPI Code Generation (MANDATORY)

> **Changed decision:** OpenAPI code generation was previously forbidden. It is now
> **required**.

- Use **OpenAPI Generator** (Maven plugin) to generate **API DTOs/models and API interfaces**.
- Do **NOT** generate a full server implementation (no generated controllers/wiring beyond interfaces).
- Generated DTOs/models are used **only at the REST adapter/controller boundary**.
- Generated DTOs **must not** become the domain model.
- Controllers **may implement** the generated API interfaces.
- The **domain stays hand-written** and independent from OpenAPI, Spring MVC and generated DTOs.
- Mapping between generated DTOs and domain/application models must be **explicit**
  (a dedicated mapper in the web adapter).

> **Dependency gate:** the `openapi-generator-maven-plugin` is NOT in pom.xml yet. Adding
> and configuring it is a task of Phase 1 and **requires approval** (no silent pom changes).

---

## Required Endpoints

| Method | Path | Purpose |
|---|---|---|
| POST | /auth/token | **Public** local demo token issuer (returns a JWT); see rule 05 |
| GET | /productos | List all (non-deleted) products; supports dynamic filtering |
| GET | /productos/{id} | Get product by ID (404 if missing or soft-deleted) |
| POST | /productos | Create a new product |
| PUT | /productos/{id} | Update an existing product |
| DELETE | /productos/{id} | Soft-delete a product (204) |

> Security: `POST /auth/token` is **public** (no bearer token). All `/productos` endpoints require a
> JWT Bearer token (`bearerAuth`): `GET` → USER or ADMIN; `POST`/`PUT`/`DELETE` → ADMIN (see rule 05).

### Dynamic Query (MANDATORY)

> **Changed decision:** the dynamic query is now **mandatory** (previously optional).

Dynamic filtering by product properties must be supported and documented in the contract.
Choose the simplest approach in the tech plan:

- enhance `GET /productos` with optional filter query parameters, **or**
- add a dedicated `GET /productos/search` endpoint.

Filtering must cover at least: `id`, `nombre`, `descripcion`, `precio`. If price-range
filtering is more useful than exact match, document that decision in the tech plan.
Dynamic queries must **exclude soft-deleted products**.

---

## Schemas (contract)

- **`ProductoRequest`** — input for create/update: `nombre`, `descripcion`, `precio` (base price).
- **`ProductoResponse`** — output, including computed tax fields:
  - `id`
  - `nombre`
  - `descripcion`
  - `precio` — base product price
  - `tipoImpuesto` — `IVA` or `ITBIS` (from active configuration)
  - `porcentajeImpuesto` — e.g. `21` or `18`
  - `importeImpuesto` — computed tax amount (`BigDecimal`, scale 2)
  - `precioConImpuesto` — base price + tax (`BigDecimal`, scale 2)
- **`ErrorResponse`** — consistent error body (code/message/details; align with tech plan).
- **`AuthRequest`** — `POST /auth/token` input: `username`, `password`.
- **`AuthResponse`** — `POST /auth/token` output: `accessToken`, `tokenType` (`Bearer`), `expiresIn` (seconds).

> Audit fields (`deleted`, `createdAt`, `updatedAt`, `deletedAt`, `deletedBy`) are **not**
> exposed in `ProductoResponse` unless explicitly approved later.

---

## Tax Exposure Rules

- Exactly **one** active tax is reflected, selected by `app.tax.type` (IVA | ITBIS).
- Do **NOT** expose multiple tax calculations simultaneously.
- Do **NOT** allow clients to choose the tax type per request.
- Persist only the **base price**; compute `importeImpuesto`/`precioConImpuesto` on read.

---

## Conventions

- Media type: `application/json`.
- `id`: auto-generated `Long`.
- `precio`/tax fields serialized from `BigDecimal` (never `double`/`float`).
- Status codes: `POST` → 201, `GET`/`PUT` → 200, `DELETE` → 204, not-found → 404,
  validation → 400, unauthenticated → 401, forbidden → 403, server error → 500.
- Security scheme: **JWT Bearer** (`bearerAuth`: `type: http, scheme: bearer, bearerFormat: JWT`),
  applied to `/productos/**`. `POST /auth/token` is public (security overridden to none). See rule 05.

---

## Validation

- Request fields validated (e.g. `nombre` not blank, `precio >= 0`).
- Validation errors return HTTP 400 with the consistent `ErrorResponse` body.

---

## What NOT to Do

- Do NOT skip the contract and write controllers first (API-first is mandatory).
- Do NOT let generated DTOs leak into the domain or application core.
- Do NOT generate full server stubs/controllers (models + API interfaces only).
- Do NOT expose raw JPA entities as API responses.
- Do NOT expose audit fields or multiple taxes in responses.

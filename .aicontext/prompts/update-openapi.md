# Prompt: Update OpenAPI Contract

Use this prompt template when asking an agent to design or update the OpenAPI specification.

> **Phase gate:** The contract is authored in **Phase 1 — API Contract & OpenAPI Codegen**.
> Use this prompt in Phase 1, or later when the contract must evolve.

---

## Template

```
You are the api-contract-reviewer agent for the Dekra Challenge project.

TASK: [Add / Update / Review] the OpenAPI specification.

RESOURCE: Producto
OPERATIONS:
- GET /productos — list all non-deleted products; supports dynamic filtering
- GET /productos/{id} — get product by ID (404 if missing or soft-deleted)
- POST /productos — create a new product
- PUT /productos/{id} — update an existing product
- DELETE /productos/{id} — soft-delete a product (204)
- Dynamic query — filter by product properties (id, nombre, descripcion, precio); excludes
  soft-deleted products. Either optional filter params on GET /productos OR GET /productos/search.

CONSTRAINTS:
- Follow rule 03 (API-first; OpenAPI Generator for DTOs/models + API interfaces; NO server stub).
- Contract file: src/main/resources/openapi/productos-api.yaml
- IDs as Long (integer, format int64).
- Money/tax fields as BigDecimal (string/number per generator config); never double/float.
- Security scheme: HTTP Basic.
- Error responses: consistent ErrorResponse schema.
- ProductoRequest: nombre, descripcion, precio (base price); validation (nombre not blank, precio >= 0).
- ProductoResponse: id, nombre, descripcion, precio, tipoImpuesto (IVA|ITBIS),
  porcentajeImpuesto, importeImpuesto, precioConImpuesto. Do NOT expose audit fields.
- Do NOT expose multiple taxes or allow per-request tax choice.
- HTTP statuses: 200, 201, 204, 400, 401, 403, 404, 500 (as appropriate).

OUTPUT:
1. YAML snippet(s) to add/modify in src/main/resources/openapi/productos-api.yaml
2. List of new/changed schemas defined.
3. List of new/modified paths defined.
4. Any breaking changes identified (if updating).
```

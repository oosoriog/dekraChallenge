# Spring Boot Guidelines

## Controllers

- Expose only required endpoints.
- Validate input DTOs.
- Delegate to application services.
- Do not place business logic in controllers.

## DTOs and mapping

- Keep request/response DTOs in the REST adapter package.
- Map between DTOs and domain models explicitly.
- Avoid leaking persistence entities to the API.

## Persistence

- Use JPA entities only in infrastructure.
- Keep repositories/adapters behind application ports if the project follows hexagonal architecture.
- Use H2 in-memory for the current assessment unless context says otherwise.

## Security

- Keep security simple and explainable.
- For basic assessments, HTTP Basic and in-memory users/roles are acceptable if approved.
- Test both unauthenticated and authenticated access.

## AOP

- Keep aspects focused.
- Avoid logging excessive framework internals.
- Do not make aspects change business behavior.

## Configuration

- Keep secrets out of source code.
- Use properties for behavior that must be configurable.
- Keep default local values reviewable and safe.

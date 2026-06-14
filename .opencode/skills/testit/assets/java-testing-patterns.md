# Java Testing Patterns

## Naming

Use descriptive names that explain behavior.

Examples:

```java
shouldCreateProductWhenDataIsValid()
shouldRejectBlankName()
shouldReturnNotFoundWhenProductDoesNotExist()
shouldRejectUnauthenticatedRequests()
```

## AAA Pattern

```java
// Arrange

// Act

// Assert
```

## Domain Tests

- Plain JUnit.
- No Spring context.
- No persistence.
- No web layer.

## Application Tests

- Use mocks/fakes for output ports.
- Verify orchestration and business behavior.
- Avoid mocking the class under test.

## REST Tests

- Use MockMvc or project-standard MVC testing.
- Verify status, JSON body, validation and security.

## Persistence Tests

- Use the configured test database.
- Verify mappings and CRUD behavior.

## Security Tests

- Unauthenticated access fails.
- Authenticated access succeeds.
- Role restrictions are enforced if roles are part of the design.

## Parameterized Tests

Use for repeated validation or boundary cases when readability improves.

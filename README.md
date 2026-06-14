# Dekra Challenge - Producto CRUD API

Spring Boot REST API for managing a `Producto` catalog. Built as a Dekra technical assessment.

## Features

- CRUD for `Producto`
- JWT Bearer authentication
- Role-based access:
  - `USER`: read operations
  - `ADMIN`: create, update and delete
- Demo token endpoint: `POST /auth/token`
- Tax calculation on read (`IVA` or `ITBIS`)
- Dynamic product search by id, name, description and price range
- Soft delete with audit information
- Execution time logging with Spring AOP
- OpenAPI contract and Swagger UI
- Docker and Docker Compose support
- GitHub Actions CI

## Tech stack

- Java 17 target (tested with JDK 17 and JDK 21)
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- H2 in-memory database
- OpenAPI Generator
- MapStruct
- Lombok
- Caffeine
- Maven
- JUnit 5, Mockito, AssertJ and MockMvc
- JaCoCo
- Docker

## Architecture

The project follows a hexagonal architecture:

```text
com.dekraChallenge.dekra_challenge
├── domain
├── application
├── adapter
│   ├── in.web
│   └── out.persistence
├── config
└── aspect
```

The domain model does not depend on Spring, JPA entities or generated OpenAPI DTOs.

The public API keeps the Spanish contract required by the challenge (`/productos`, `nombre`,
`descripcion`, `precio`, etc.), while the internal code uses English names.

## Scope and trade-offs

This project includes a few decisions that go beyond what a simple CRUD would normally need. They
were added intentionally to show a more maintainable and production-oriented structure. Some of those decisions are:

- **Hexagonal architecture**: keeps the domain independent from Spring, JPA and generated DTOs.

- **Language boundary**: the public API keeps the Spanish names required by the challenge
  (`/productos`, `nombre`, `descripcion`, `precio`, etc.), while the internal code uses English names
  for domain classes, services, repositories and tax calculators. This keeps the external contract
  aligned with the requirement without making the internal code harder to maintain.

- **OpenAPI-first contract**: makes the external API explicit and stable, with generated DTOs and API
  interfaces.

- **JWT security**: uses a local demo token endpoint and role-based access without adding an external
  identity provider.

- **Soft delete and audit fields**: keeps deletion history instead of physically removing rows.

- **MapStruct**: keeps mappings explicit and compile-time checked.

- **AOP execution-time logging**: adds basic observability without mixing timing logs with business
  code.

- **Docker, CI, cache and database indexes**: included as small production-readiness examples, even
  though they are not strictly needed for an in-memory CRUD assessment.

- **AI development context**: `.aicontext/` is included to document how future AI-assisted changes should respect
  the existing architecture, naming conventions and scope.

External databases, Kubernetes, external identity providers and distributed observability are
intentionally out of scope.

## AI-assisted development context

The repository includes a small AI development context to make future changes easier to understand
and safer to implement with AI coding assistants.

These files document the project architecture, naming conventions, layer boundaries and development
rules. They are not required to build, run or test the application.

Main files:

- `.aicontext/`: project context for future AI-assisted changes.
- `.opencode/skills/`: OpenCode skills used as reusable development workflows.
- `opencode.json`: OpenCode configuration that points the assistant to the project context.

The goal is to help future AI-assisted work stay aligned with the current design instead of
rediscovering the project structure from scratch.

## Requirements

- JDK 17 or 21
- Docker, optional
- `make`, optional

The Maven wrapper is included, so a local Maven installation is not required.

## Configuration

The JWT secret is required and must be provided externally. It must have at least 32 characters.

Example value for local execution:

```bash
APP_SECURITY_JWT_SECRET=my-demo-secret-at-least-32-characters-long-0123456789
```

Main configuration variables:

| Variable | Description | Default |
|---|---|---|
| `APP_SECURITY_JWT_SECRET` | JWT signing secret. Required. Minimum 32 characters. | none |
| `APP_TAX_TYPE` | Active tax calculator: `IVA` or `ITBIS`. | `ITBIS` |
| `APP_SECURITY_JWT_ISSUER` | JWT issuer. | `dekra-challenge` |
| `APP_SECURITY_JWT_EXPIRATION_SECONDS` | Token expiration in seconds. | `3600` |
| `APP_DEMO_DATA_ENABLED` | Enables demo product seed data. | `true` |

For Docker Compose, create a local `.env` file with at least this value:

```bash
APP_SECURITY_JWT_SECRET=my-demo-secret-at-least-32-characters-long-0123456789
```

## Makefile usage

The project includes a Makefile as a convenience wrapper around the Maven and Docker commands.

```bash
make help
```

Common targets:

| Command | Description |
|---|---|
| `make test` | Run the test suite |
| `make verify` | Run Maven `clean verify` |
| `make build` | Package the application skipping tests |
| `make up` | Build and start Docker Compose in detached mode |
| `make down` | Stop Docker Compose |
| `make restart` | Restart Docker Compose |
| `make logs` | Follow application logs |
| `make ci` | Run the local Maven verification target |

The raw Maven and Docker commands are also shown below for environments where `make` is not
available.

## Manual commands

Build:

```bash
./mvnw clean package
```

Run tests:

```bash
./mvnw clean test
```

Run Maven verification:

```bash
./mvnw clean verify
```

Run the stricter quality profile:

```bash
./mvnw clean verify -Pquality
```

The `quality` profile runs Checkstyle and enforces JaCoCo coverage limits.

## Run locally

Linux/macOS:

```bash
export APP_SECURITY_JWT_SECRET=my-demo-secret-at-least-32-characters-long-0123456789
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
$env:APP_SECURITY_JWT_SECRET="my-demo-secret-at-least-32-characters-long-0123456789"
.\mvnw.cmd spring-boot:run
```

The application starts at:

```text
http://localhost:8080
```

## Docker

Create a local `.env` file first:

```bash
APP_SECURITY_JWT_SECRET=my-demo-secret-at-least-32-characters-long-0123456789
```

Start the application:

```bash
make up
```

Or without Makefile:

```bash
docker compose up --build -d
```

Stop the application:

```bash
make down
```

Or without Makefile:

```bash
docker compose down --remove-orphans
```

Build only the Docker image:

```bash
docker build -t dekra-challenge .
```

Run the Docker quality stage:

```bash
docker build --target quality .
```

The application uses H2 in-memory storage, so data is reset when the application restarts.

## Swagger UI

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

Use `POST /auth/token` to get a JWT token, then click `Authorize` and paste the raw token.

## Demo users

| Username | Password | Role |
|---|---|---|
| `user` | `user-password` | `USER` |
| `admin` | `admin-password` | `ADMIN` |

`POST /auth/token` is only a local demo endpoint for the assessment. In a real environment this would
be replaced by an external identity provider.

## Tax calculation

The active tax calculator is selected with `APP_TAX_TYPE`.

Supported values:

| Type | Rate |
|---|---|
| `IVA` | 21% |
| `ITBIS` | 18% |

Only the base price is stored. Tax fields are calculated when products are returned by the API.

## Database

The project uses H2 in-memory database.

H2 console:

```text
http://localhost:8080/h2-console
```

Connection:

```text
JDBC URL: jdbc:h2:mem:dekra_challenge
User: sa
Password:
```

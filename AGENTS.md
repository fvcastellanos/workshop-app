# WorkShopERP

Spring Boot 3.3.4 + Vaadin 24.5.2 + Java 21. Maven wrapper (`./mvnw`).

## Quick commands

```
./mvnw clean test              # unit tests only (Surefire, **/*Test.java)
./mvnw clean test verify       # unit + integration tests (Failsafe, **/*IT.java)
./mvnw clean package -Pproduction -DskipTests   # production build with Vaadin frontend bundle
```

## Required env vars

| Var | Purpose |
|---|---|
| `DATASOURCE_URL` | JDBC URL with schema e.g. `jdbc:postgresql://host:5432/db?user=u&password=p&currentSchema=workshop` |
| `AUTH0_ISSUER` | Auth0 issuer URL |
| `AUTH0_CLIENT_ID` | Auth0 client ID |
| `AUTH0_CLIENT_SECRET` | Auth0 client secret |
| `WORKSHOP_CORS_ORIGINS` | Comma-separated allowed CORS origins |

## Database

- Custom schema `workshop` (set via `spring.datasource.url` or `dbShema` property)
- Flyway migrations: SQL in `resources/db/migration/`, Java in `java/db/migration/`
- Dev helpers:
  ```
  ./scripts/db-migrate.sh    # flyway:migrate (requires DATASOURCE_URL)
  ./scripts/db-clean.sh      # flyway:clean
  ```
- Local Postgres via `docker compose -f docker/services.yaml up -d`

## Architecture

- **Security**: Auth0 OAuth2 via Okta starter. `VaadinWebSecurity` subclass protects all routes except `GET /actuator/**`.
- **UI**: Vaadin Flow (server-side Java), theme `resta`. Views under `views/`, layouts in `views/layouts/`.
- **Persistence**: Spring Data JPA. Entities in `model.entity/`, repos in `model.repository/` and `sequence.model.repository/`.
- **Service layer**: `service/` packages. Domain exceptions: `BusinessException` (with HTTP status), `ValidationException`.
- **Events**: `event/` package with listener/processor pattern for cross-cutting logic (inventory, work orders).
- **Sequence generation**: Custom sequence module under `sequence/` (separate from DB sequences).

## Code conventions

- Use Java 21 features like type inference, switch statements
- Add `final` modifier to method parameters and variables which references are not planned to be changed
- Use explicit imports, avoid using star imports

## Testing conventions

- Unit tests: `@ExtendWith(MockitoExtension.class)`, AssertJ assertions, `verifyNoMoreInteractions`
- Integration tests: `@SpringBootTest`, require running Postgres
- Coverage: JaCoCo (reports in `target/site/jacoco/`)

## CI

- GitHub Actions: `feature/**` and `develop` branches run `mvn clean test verify` with Postgres service container
- SonarQube scan on PRs to `develop`
- Jenkins: same flow inside Docker (maven:3.9-eclipse-temurin-21)
- Release: GitFlow (develop → release/x.y.z → main + tag), via `Jenkinsfile.release` or GitHub Actions `release.yml`

## Vaadin notes

- Production builds need `-Pproduction` profile (triggers `vaadin:build-frontend`)
- Frontend generated files in `src/main/frontend/generated/` (gitignored via `**/frontend/generated/`)
- Dev mode includes Vaadin dev tools; excluded in production profile

# WorkShopERP

Spring Boot 3.5.14 + Vaadin 24.10.4 + Java 25. Maven wrapper (`./mvnw`).

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

- Custom schema `workshop` (set via `spring.datasource.url` `currentSchema` param or the `dbShema` property — note the intentional typo in `DataSourceConfiguration`)
- The `workshop` schema **must exist before** Flyway runs: migrations create tables as `workshop.*` and set `OWNER TO workshop`. CI pipelines create it explicitly (`CREATE SCHEMA IF NOT EXISTS workshop;`).
- Flyway 12.5.0 migrations: SQL in `resources/db/migration/` (V1), Java in `java/db/migration/` (V2–V12, extend `db.migration.FlywayMigration`)
- Dev helpers:
  ```
  ./scripts/db-migrate.sh    # flyway:migrate (requires DATASOURCE_URL)
  ./scripts/db-clean.sh      # flyway:clean
  ```
- Local Postgres via `docker compose -f docker/services.yaml up -d`

## Architecture

- **Security**: Auth0 OAuth2 via Okta starter. `ApplicationSecurityConfiguration` (`@EnableWebSecurity` + `SecurityFilterChain` bean, not a `VaadinWebSecurity` subclass) requires auth on all routes except `GET /actuator/**`; login page at `/oauth2/authorization/okta`. Auth0 helpers in `security/auth0/`.
- **UI**: Vaadin Flow (server-side Java), theme `resta` (`@Theme("resta")`). Views under `views/`, layouts in `views/layouts/`.
- **Persistence**: Spring Data JPA + HikariCP. Entities in `model/entity/`, repos in `model/repository/` and `sequence/model/repository/`.
- **Service layer**: `service/` packages. Domain exceptions: `BusinessException` (with HTTP status), `ValidationException`, `AuthenticationException`.
- **Events**: `event/` package with listener/processor pattern for cross-cutting logic (inventory, work orders).
- **Sequence generation**: Custom sequence module under `sequence/` (separate from DB sequences), uses KSUID (`TimeBasedGenerator`).

## Containerization & deployment

- **Dockerfile** (`docker/Dockerfile`): multi-stage — builder `maven:3.9-eclipse-temurin-25` → runner `azul/zulu-openjdk:25-jre-latest`, non-root user `workshop`, `EXPOSE 8080`.
- **Entrypoint** (`docker/entrypoint.sh`): minimal, just `exec java -Xms128M -Xmx128M -jar workshop-app.jar "$@"`. Flyway migrations still run automatically on Spring Boot startup.
- **Helm chart** (`k8s/`): image `registry.devcoregt.com/apps/workshop-app`, references existing ConfigMap (`workshop-app-config`) and Secret (`workshop-app-secrets`). Probes on `/actuator/health:8080`.

## Code conventions

- Use Java 25 features like type inference, switch statements
- Add `final` modifier to method parameters and variables which references are not planned to be changed
- Use explicit imports, avoid using star imports

## Testing conventions

- Unit tests: `@ExtendWith(MockitoExtension.class)`, AssertJ assertions, `verifyNoMoreInteractions`
- Integration tests: `@SpringBootTest`, require running Postgres
- Coverage: JaCoCo (reports in `target/site/jacoco/`)

## CI

- GitHub Actions: `feature/**` and `develop` branches run `mvn clean test verify` with Postgres service container
- SonarQube scan on PRs to `develop`
- Jenkins: same flow inside Docker (maven:3.9-eclipse-temurin-25)
- Release: GitFlow (develop → release/x.y.z → main + tag), via `Jenkinsfile.release` or GitHub Actions `release.yml`

## Vaadin notes

- Production builds need `-Pproduction` profile (triggers `vaadin:build-frontend`)
- Frontend generated files in `src/main/frontend/generated/` (gitignored via `**/frontend/generated/`)
- Dev mode includes Vaadin dev tools; excluded in production profile

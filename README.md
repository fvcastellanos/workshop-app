# WorkShopERP

Sistema ERP para talleres automotrices (Repuestos y Servicios Técnicos Automotrices), construido con **Spring Boot** y **Vaadin Flow**.

Aplicación web de servidor (Java) para la gestión de clientes, proveedores, marcas/líneas de vehículos, productos e inventario, órdenes de trabajo y facturación, con autenticación OAuth2/OIDC vía **Auth0**.

## Stack tecnológico

| Componente | Versión |
|---|---|
| Java | 25 |
| Spring Boot | 3.5.14 |
| Vaadin | 24.10.4 |
| Flyway | 12.5.0 |
| PostgreSQL | 17 |
| Build | Maven (wrapper `./mvnw`) |

## Requisitos previos

- **JDK 25** (recomendado [SDKMAN](https://sdkman.io/); ver `.sdkmanrc` con `java=25.0.3-zulu`)
- **Docker** + Docker Compose (para PostgreSQL local)
- **PostgreSQL 17** (si no usas el contenedor)

## Variables de entorno

| Variable | Descripción |
|---|---|
| `DATASOURCE_URL` | URL JDBC con schema, p. ej. `jdbc:postgresql://localhost:5432/workshop?user=workshop&password=...&currentSchema=workshop` |
| `AUTH0_ISSUER` | URL del issuer de Auth0 |
| `AUTH0_CLIENT_ID` | Client ID de Auth0 |
| `AUTH0_CLIENT_SECRET` | Client secret de Auth0 |
| `WORKSHOP_CORS_ORIGINS` | Orígenes CORS permitidos, separados por coma |

## Puesta en marcha (desarrollo local)

1. Clona el repositorio y activa el JDK con SDKMAN:

   ```bash
   git clone https://github.com/fvcastellanos/workshop-app.git
   cd workshop-app
   sdk env
   ```

2. Levanta PostgreSQL local:

   ```bash
   docker compose -f docker/services.yaml up -d
   ```

3. Crea el schema `workshop` (requerido por Flyway):

   ```bash
   docker exec -i postgres psql -U workshop -d workshop -c "CREATE SCHEMA IF NOT EXISTS workshop;"
   ```

4. Exporta las variables de entorno y arranca en modo desarrollo:

   ```bash
   export DATASOURCE_URL="jdbc:postgresql://localhost:5432/workshop?user=workshop&password=workshop&currentSchema=workshop"
   export AUTH0_ISSUER="..."
   export AUTH0_CLIENT_ID="..."
   export AUTH0_CLIENT_SECRET="..."
   export WORKSHOP_CORS_ORIGINS="http://localhost:8080"

   ./mvnw spring-boot:run
   ```

La aplicación queda disponible en `http://localhost:8080` (login vía Auth0).

## Compilación y pruebas

```bash
./mvnw clean test              # pruebas unitarias (Surefire, **/*Test.java)
./mvnw clean test verify       # unitarias + integración (Failsafe, **/*IT.java, requiere Postgres)
./mvnw clean package -Pproduction -DskipTests   # build de producción con frontend Vaadin
```

- **Cobertura**: JaCoCo, reportes en `target/site/jacoco/`.

## Base de datos

- Schema custom **`workshop`** (se configura vía `currentSchema` en `DATASOURCE_URL` o la propiedad `dbShema` en `DataSourceConfiguration`).
- El schema `workshop` **debe existir antes** de ejecutar Flyway: las migraciones crean las tablas como `workshop.*` y asignan `OWNER TO workshop`.
- **Migraciones Flyway**: SQL en `src/main/resources/db/migration/` (V1) y Java en `src/main/java/db/migration/` (V2–V12).
- Helpers de desarrollo:

  ```bash
  ./scripts/db-migrate.sh    # flyway:migrate (requiere DATASOURCE_URL)
  ./scripts/db-clean.sh      # flyway:clean
  ```

## Arquitectura

```
src/main/java/net/cavitos/workshop/
├── configuration/     # Configuración (DataSource, reloj)
├── domain/            # Modelos de dominio, excepciones, validadores
├── event/             # Listeners/processors (inventario, órdenes de trabajo)
├── factory/           # Factories
├── model/
│   ├── entity/        # Entidades JPA
│   ├── generator/     # Generadores de IDs (KSUID)
│   └── repository/    # Repositorios Spring Data JPA
├── resource/          # Recursos (ImageLoader)
├── security/          # Seguridad (Auth0, OAuth2, usuarios)
├── sequence/          # Módulo de generación de secuencias
├── service/           # Capa de servicios
├── transformer/       # Transformers DTO <-> entidad
└── views/             # Vistas Vaadin Flow (tema `resta`)
```

- **Seguridad**: Auth0 OAuth2/OIDC vía Okta starter. Todas las rutas requieren autenticación excepto `GET /actuator/**`. Login en `/oauth2/authorization/okta`.
- **Persistencia**: Spring Data JPA + HikariCP.
- **Eventos**: patrón listener/processor para lógica transversal (inventario y órdenes de trabajo).
- **Secuencias**: generación propia de secuencias (`sequence/`) basada en KSUID (`TimeBasedGenerator`), separada de las secuencias de PostgreSQL.

## Contenerización (Docker)

```bash
docker build -f docker/Dockerfile -t workshop-app .
docker run -p 8080:8080 \
  -e DATASOURCE_URL="..." \
  -e AUTH0_ISSUER="..." \
  -e AUTH0_CLIENT_ID="..." \
  -e AUTH0_CLIENT_SECRET="..." \
  -e WORKSHOP_CORS_ORIGINS="..." \
  workshop-app
```

- **Dockerfile** (`docker/Dockerfile`): multi-stage — builder `maven:3.9-eclipse-temurin-25` → runner `azul/zulu-openjdk:25-jre-latest`, usuario no-root `workshop`.
- **Entrypoint** (`docker/entrypoint.sh`): arranca `java -Xms128M -Xmx128M -jar workshop-app.jar`. Las migraciones Flyway se ejecutan automáticamente al iniciar Spring Boot.

> El schema `workshop` debe existir en la base de datos de destino antes de arrancar el contenedor.

## Despliegue (Kubernetes / Helm)

El chart Helm está en `k8s/` (imagen `registry.devcoregt.com/apps/workshop-app`).

```bash
helm upgrade --install workshop-app ./k8s \
  --set image.tag=<version> \
  --set configmap.auth0Issuer="..." \
  --set configmap.corsOrigins="..."
```

El chart referencia un ConfigMap (`workshop-app-config`) y un Secret (`workshop-app-secrets`) existentes en el cluster. Incluye probes de liveness/readiness sobre `/actuator/health:8080` y servicio de tipo `NodePort` (puerto `30082`).

## CI/CD

- **GitHub Actions**: `feature/**` y `develop` ejecutan `mvn clean test verify` con un contenedor de Postgres.
- **SonarQube**: análisis estático en PRs hacia `develop`.
- **Jenkins**: mismo flujo dentro de Docker (`maven:3.9-eclipse-temurin-25`).
- **Release**: flujo GitFlow (`develop` → `release/x.y.z` → `main` + tag), vía `Jenkinsfile.release` o el workflow `release.yml`.

## Modelo de datos

Los diagramas entidad-relación están en `database/model/` (MySQL Workbench `.mwb` y exportaciones `.dbm`/`.png`).

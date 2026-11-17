# Customers API — Ciclo de vida (dev / test / prod)

API REST simple para crear y listar clientes, con perfiles Spring y Docker por ambiente.  
Persistencia actual: **lista en memoria** (sin SQL).

## Requisitos

- JDK 17+
- Maven Wrapper incluido (`mvnw` / `mvnw.cmd`) — no hace falta instalar Maven global
- Docker Desktop (solo si usas contenedores)
- Postman (opcional, para probar endpoints)

## Dependencias (descargar / instalar)

Desde la raíz del proyecto:

```bash
# Windows
.\mvnw.cmd dependency:resolve

# Linux / macOS
./mvnw dependency:resolve
```

La primera vez que compiles (`package` o `spring-boot:run`), Maven también descarga todo lo del `pom.xml`.

## Limpiar el proyecto

```bash
# Windows
.\mvnw.cmd clean

# Linux / macOS
./mvnw clean
```

Borra la carpeta `target/` (clases compiladas y JARs previos).

## Construir (generar el JAR)

```bash
# Windows
.\mvnw.cmd clean package

# Linux / macOS
./mvnw clean package
```

JAR generado:

```text
target/customers-0.0.1-SNAPSHOT.jar
```

Saltar tests (solo si hace falta en una demo):

```bash
.\mvnw.cmd clean package -DskipTests
```

---

## Ejecutar SIN Docker

### Opción A — con Maven (desarrollo)

```bash
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=test"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=prod"
```

### Opción B — con el JAR (como pide el reto)

```bash
# DEV  → http://localhost:8080
java -jar target\customers-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# TEST → http://localhost:8081
java -jar target\customers-0.0.1-SNAPSHOT.jar --spring.profiles.active=test

# PROD → http://localhost:9090
java -jar target\customers-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

Al arrancar verás en consola el nombre de la app, el puerto y el mensaje del ambiente (`Ejecutando en DEV` / `TEST` / `PROD`).

| Ambiente | Puerto | Nombre app | URL base |
|----------|--------|------------|----------|
| dev | 8080 | customers-dev | http://localhost:8080 |
| test | 8081 | customers-test | http://localhost:8081 |
| prod | 9090 | customers-prod | http://localhost:9090 |

---

## Ejecutar CON Docker

Un Compose **por ambiente** (no levantes todos a la vez).

```bash
# DEV
docker compose -f docker-compose.dev.yml up --build

# TEST
docker compose -f docker-compose.test.yml up --build

# PROD
docker compose -f docker-compose.prod.yml up --build
```

Parar:

```bash
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.test.yml down
docker compose -f docker-compose.prod.yml down
```

Logs:

```bash
docker compose -f docker-compose.dev.yml logs -f
```

Las URLs son las mismas que sin Docker (8080 / 8081 / 9090).

---

## Endpoints (contratos)

Base path: `/api/customers`

### Crear cliente

```http
POST /api/customers
Content-Type: application/json
```

**Request**

```json
{
  "name": "Juan Perez",
  "email": "juan@email.com"
}
```

**Response `201 Created`**

```json
{
  "id": 1,
  "name": "Juan Perez",
  "email": "juan@email.com"
}
```

**Errores posibles**

| Código | Cuándo |
|--------|--------|
| `400` | Validación (`name`/`email` vacíos o email inválido) |
| `409` | Email ya existe |

### Listar clientes

```http
GET /api/customers
```

**Response `200 OK`**

```json
[
  {
    "id": 1,
    "name": "Juan Perez",
    "email": "juan@email.com"
  }
]
```

Ejemplos con curl (DEV):

```bash
curl -X POST http://localhost:8080/api/customers ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Juan Perez\",\"email\":\"juan@email.com\"}"

curl http://localhost:8080/api/customers
```

---

## Postman

1. Abre Postman → **Import**
2. Selecciona: `docs/Customers (not SQL).postman_collection.json`
3. Carpetas: **DEV** / **TEST** / **PROD** (Create + List en cada una)
4. Variables de colección:

| Variable | Valor |
|----------|--------|
| `baseUrlDev` | `http://localhost:8080` |
| `baseUrlTest` | `http://localhost:8081` |
| `baseUrlProd` | `http://localhost:9090` |

---

## Estructura rápida

```text
src/main/java/com/example/customers/
  controller/   → endpoints HTTP
  service/      → lógica de negocio
  repository/   → lista en memoria
  dto/          → request / response
  model/        → Customer
  config/       → CORS + log de ambiente
docker-compose.dev.yml | .test.yml | .prod.yml
docs/                   → colección Postman
```

## Nota

Los datos viven en memoria: al reiniciar la app (o el contenedor) la lista queda vacía. Eso es esperado en esta versión (sin SQL).

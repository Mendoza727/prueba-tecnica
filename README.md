# Customers API — Ciclo de vida (dev / test / prod)

API REST para crear y listar clientes con **Spring Boot 4**, perfiles por ambiente, **JPA** y Docker (un Compose por ambiente).

| Ambiente | Puerto app | Base de datos | Compose |
|----------|------------|---------------|---------|
| **dev** | 8080 | H2 en memoria | `docker-compose.dev.yml` |
| **test** | 8081 | H2 en memoria | `docker-compose.test.yml` |
| **prod** (simulado) | 9090 | PostgreSQL | `docker-compose.prod.yml` |

---

## Requisitos

- JDK 17+
- Maven Wrapper (`mvnw` / `mvnw.cmd`)
- Docker Desktop (para contenedores / Postgres de prod)
- Postman (opcional)

---

## Dependencias

```bash
# Windows
.\mvnw.cmd dependency:resolve

# Linux / macOS
./mvnw dependency:resolve
```

También se descargan al hacer `package` o `spring-boot:run`.

---

## Limpiar

```bash
.\mvnw.cmd clean
```

Borra `target/`.

---

## Construir (JAR)

```bash
.\mvnw.cmd clean package
```

Salida: `target/customers-0.0.1-SNAPSHOT.jar`

Sin tests (solo demo):

```bash
.\mvnw.cmd clean package -DskipTests
```

---

## Tests

```bash
.\mvnw.cmd test
```

Incluye: unitarios del service, `@WebMvcTest` del controller, `@DataJpaTest` del repository y carga de contexto.

---

## Ejecutar SIN Docker

### Con Maven

```bash
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=test"
```

### Con el JAR

```bash
# DEV  → http://localhost:8080
java -jar target\customers-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# TEST → http://localhost:8081
java -jar target\customers-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
```

Al arrancar verás en consola: nombre de app, puerto y mensaje (`Ejecutando en DEV` / `TEST` / `PROD`).

| Ambiente | Puerto | Nombre app | URL base |
|----------|--------|------------|----------|
| dev | 8080 | customers-dev | http://localhost:8080 |
| test | 8081 | customers-test | http://localhost:8081 |
| prod | 9090 | customers-prod | http://localhost:9090 |

### H2 Console (solo DEV)

- URL: http://localhost:8080/h2-console  
- JDBC URL: `jdbc:h2:mem:customers_dev`  
- User: `sa` · Password: (vacío)

---

## Ambientes y properties

| Archivo | Uso |
|---------|-----|
| `application.properties` | Compartido; perfil por defecto `dev` |
| `application-dev.properties` | H2 + consola H2 |
| `application-test.properties` | H2 limpia (`create-drop`) |
| `application-prod.properties` | Postgres vía `DB_URL` / `DB_USER` / `DB_PASSWORD` |

### Variables de entorno (PROD)

Obligatorias para el perfil `prod` (no hay default en el properties):

| Variable | En Docker Compose (prod) | App local → Postgres Docker |
|----------|--------------------------|-----------------------------|
| `DB_URL` | `jdbc:postgresql://db:5432/customers` | `jdbc:postgresql://localhost:5433/customers` |
| `DB_USER` | `customers` | `customers` |
| `DB_PASSWORD` | `customers` | `customers` |

Plantilla: `.env.example` (no subas un `.env` real; ya está en `.gitignore`).

### PROD local: app en PC + Postgres en Docker

```bash
# Solo la base (puerto HOST 5433 para no chocar con un Postgres local en 5432)
docker compose -f docker-compose.prod.yml up -d db

# Windows PowerShell — exportar env y arrancar
$env:DB_URL="jdbc:postgresql://localhost:5433/customers"
$env:DB_USER="customers"
$env:DB_PASSWORD="customers"
java -jar target\customers-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## Ejecutar CON Docker

**Un Compose por ambiente** (no mezclar todos en un solo archivo).

```bash
# DEV (H2 dentro de la app)
docker compose -f docker-compose.dev.yml up --build

# TEST
docker compose -f docker-compose.test.yml up --build

# PROD (app + PostgreSQL)
docker compose -f docker-compose.prod.yml up --build
```

Parar:

```bash
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.test.yml down
docker compose -f docker-compose.prod.yml down

# Borrar también el volumen de Postgres (pierdes datos):
docker compose -f docker-compose.prod.yml down -v
```

Logs:

```bash
docker compose -f docker-compose.dev.yml logs -f
docker compose -f docker-compose.prod.yml logs -f
```

### Conectar un gestor SQL a Postgres (PROD)

El contenedor publica **5433** en tu máquina (dentro del contenedor sigue siendo 5432).

| Campo | Valor |
|--------|--------|
| Host | `localhost` |
| Port | **5433** |
| Database | `customers` |
| User | `customers` |
| Password | `customers` |
| SSL | desactivado |

Consulta rápida:

```bash
docker exec -it customers-db psql -U customers -d customers -c "SELECT * FROM customers;"
```

---

## Endpoints (contratos)

Base: `/api/customers`

### Crear cliente

```http
POST /api/customers
Content-Type: application/json
```

**Request**

```json
{
  "name": "Juan Perez",
  "email": "juan@email.com",
  "phone": "+573001234567"
}
```

**Response `201 Created`**

```json
{
  "id": 1,
  "name": "Juan Perez",
  "email": "juan@email.com",
  "phone": "+573001234567",
  "createdAt": "2026-11-17T02:00:00.000+00:00",
  "updatedAt": "2026-11-17T02:00:00.000+00:00"
}
```

**Errores**

| Código | Cuándo |
|--------|--------|
| `400` | Validación (`name` / `email` / `phone` inválidos o vacíos) |
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
    "email": "juan@email.com",
    "phone": "+573001234567",
    "createdAt": "2026-11-17T02:00:00.000+00:00",
    "updatedAt": "2026-11-17T02:00:00.000+00:00"
  }
]
```

### curl (ejemplo DEV)

```bash
curl -X POST http://localhost:8080/api/customers ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Juan Perez\",\"email\":\"juan@email.com\",\"phone\":\"+573001234567\"}"

curl http://localhost:8080/api/customers
```

### curl (ejemplo PROD Docker)

```bash
curl -X POST http://localhost:9090/api/customers ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Juan Perez\",\"email\":\"juan@emailprod.com\",\"phone\":\"+573001234567\"}"

curl http://localhost:9090/api/customers
```

---

## Postman

1. Postman → **Import**
2. Archivo: `docs/Customers (PROD - ONLY SQL).postman_collection.json`
3. Carpetas **DEV** / **TEST** / **PROD** (Create + List)
4. Variables de colección:

| Variable | Valor |
|----------|--------|
| `baseUrlDev` | `http://localhost:8080` |
| `baseUrlTest` | `http://localhost:8081` |
| `baseUrlProd` | `http://localhost:9090` |

El body de create incluye `name`, `email` y `phone`.

---

## Estructura

```text
customers/
├── Dockerfile
├── docker-compose.dev.yml
├── docker-compose.test.yml
├── docker-compose.prod.yml   # app + Postgres (host 5433 → container 5432)
├── .env.example
├── docs/
│   └── Customers (PROD - ONLY SQL).postman_collection.json
└── src/main/java/com/example/customers/
    ├── controller/   → POST/GET /api/customers
    ├── service/      → lógica de negocio
    ├── repository/   → JpaRepository
    ├── dto/          → request / response
    ├── model/        → @Entity Customer
    ├── config/       → CORS + log de ambiente
    └── exception/    → errores de validación / conflicto
```

---

## Nota sobre datos

- **dev / test (H2 mem):** al reiniciar la app se pierden los datos.
- **prod (Postgres + volumen `pgdata`):** los datos persisten hasta `docker compose -f docker-compose.prod.yml down -v`.

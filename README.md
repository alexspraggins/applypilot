# ApplyPilot

Job application tracking app with a Spring Boot backend, JWT auth, and a PostgreSQL database.

## Run With Docker

Start the full stack from the repository root (run containers & rebuild containers):

```bash
docker compose up
```

```bash
docker compose up --build
```

This starts:

- `postgres` on port `5432`
- `backend` on port `8080`

The backend API will be available at:

```text
http://localhost:8080
```

## Stop The Stack

```bash
docker compose down
```

## Run Tests

Run the backend tests locally:

```bash
cd backend
./mvnw test
```

The test suite uses an in-memory H2 database, so it does not require the Docker Postgres container.

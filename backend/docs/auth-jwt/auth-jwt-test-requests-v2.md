# Auth + JWT Manual Test Requests V2

These requests match the current implementation, where:

- Users can register and log in through `/api/auth/**`.
- Register/login responses include a JWT.
- `/api/applications/**` is protected by Spring Security.
- The backend reads the authenticated user from the JWT and uses that user for application ownership.
- Application requests no longer need `userId` in the body or query string.

## Prerequisites

From the project root, start Postgres with Docker Compose:

```bash
docker compose up -d
```

Then start the backend:

```bash
cd backend
./mvnw spring-boot:run
```

The backend should run on:

```text
http://localhost:8080
```

## Health Check

```bash
curl http://localhost:8080/health
```

Expected: a successful response from the health endpoint.

## Register User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }'
```

Expected: response includes `token`, `userId`, `name`, `email`, and `message`.

## Login User

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

Expected: response includes a JWT token.

Save the token:

```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjo3LCJuYW1lIjoiVGVzdCBVc2VyIiwiaWF0IjoxNzc5NjgxNTAwLCJleHAiOjE3Nzk3Njc5MDB9.6vNzsOLCAHuDpD4_JnViijFPNB4euLBPrO93GzqEPpeszBFJMaiNlVu8R6FB-uUXpEtWaIFTsuM97XEn99mUTw"
```

## Invalid Login

```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "wrong-password"
  }'
```

Expected: `400 Bad Request` with an invalid email/password message.

## Protected Endpoint Without JWT

```bash
curl -i http://localhost:8080/api/applications
```

Expected: request is rejected with `401` or `403`.

## List Applications With JWT

```bash
curl -i http://localhost:8080/api/applications \
  -H "Authorization: Bearer $TOKEN"
```

Expected: request reaches the protected endpoint and returns the logged-in user's applications.

## Create Application With JWT

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "company": "OpenAI",
    "position": "Backend Engineer",
    "location": "Remote",
    "jobUrl": "https://example.com/jobs/backend",
    "status": "APPLIED",
    "appliedDate": "2026-05-21",
    "notes": "Testing protected create endpoint"
  }'
```

Expected: response includes the created application and the current authenticated user as owner.

```bash
APP_ID=5
```

## Get Application By ID

```bash
curl http://localhost:8080/api/applications/$APP_ID \
  -H "Authorization: Bearer $TOKEN"
```

Expected: response includes the application with id `$APP_ID`, if it belongs to the logged-in user.

## Update Application

```bash
curl -X PUT http://localhost:8080/api/applications/$APP_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "company": "OpenAI",
    "position": "Backend Engineer",
    "location": "Remote",
    "jobUrl": "https://example.com/jobs/backend",
    "status": "INTERVIEWING",
    "appliedDate": "2026-05-21",
    "notes": "Updated after JWT test"
  }'
```

Expected: response includes the updated application if it belongs to the logged-in user.

## Delete Application

```bash
curl -X DELETE -i http://localhost:8080/api/applications/$APP_ID \
  -H "Authorization: Bearer $TOKEN"
```

Expected: `204 No Content` if the application belongs to the logged-in user.

## Notes

The current JWT filter validates the bearer token and sets the authenticated `User` in Spring Security's context.

The current application endpoints are JWT-owned, so ownership is derived from the authenticated user rather than from request data.

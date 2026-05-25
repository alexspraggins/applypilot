# Auth + JWT Manual Test Requests

These requests match the current implementation, where:

- Users can register and log in through `/api/auth/**`.
- Register/login responses include a JWT.
- `/api/applications/**` is protected by Spring Security.
- Application endpoints still require `userId` in request/query data.
- The JWT is required to access application endpoints, but ownership is not yet derived from the token.

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
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjo2LCJuYW1lIjoiVGVzdCBVc2VyIiwiaWF0IjoxNzc5Njc5ODQ2LCJleHAiOjE3Nzk3NjYyNDZ9.M0kuM_3p7jMbkgLoYeIzCUD2FI5PcVzhsHnaHuv-L2lTdS037BX3Ta6M40T4Iu8vMM_aWwIUalO1DXEe5mYQTA"
APP_ID=1
USER_ID=6
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

Current implementation requires `userId` as a query parameter.

```bash
curl -i "http://localhost:8080/api/applications?userId=$USER_ID" \
  -H "Authorization: Bearer $TOKEN"
```

Expected: request reaches the protected endpoint and returns the user's applications.

## Create Application With JWT

Current implementation requires `userId` in the request body.

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"company\": \"OpenAI\",
    \"position\": \"Backend Engineer\",
    \"location\": \"Remote\",
    \"jobUrl\": \"https://example.com/jobs/backend\",
    \"status\": \"APPLIED\",
    \"appliedDate\": \"2026-05-21\",
    \"notes\": \"Testing protected create endpoint\",
    \"userId\": $USER_ID
  }"
```

Expected: response includes the created application.

## Get Application By ID

```bash
curl http://localhost:8080/api/applications/$APP_ID \
  -H "Authorization: Bearer $TOKEN"
```

Expected: response includes the application with id `$APP_ID`, if it exists.

## Update Application

```bash
curl -X PUT http://localhost:8080/api/applications/$APP_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"company\": \"OpenAI\",
    \"position\": \"Backend Engineer\",
    \"location\": \"Remote\",
    \"jobUrl\": \"https://example.com/jobs/backend\",
    \"status\": \"INTERVIEWING\",
    \"appliedDate\": \"2026-05-21\",
    \"notes\": \"Updated after JWT test\",
    \"userId\": $USER_ID
  }"
```

Expected: response includes the updated application.

## Delete Application

```bash
curl -X DELETE -i http://localhost:8080/api/applications/$APP_ID \
  -H "Authorization: Bearer $TOKEN"
```

Expected: `204 No Content` if the application exists.

## Notes

The current JWT filter validates the bearer token and sets the authenticated user in Spring Security's context.

The current application endpoints are protected, but they still rely on `userId` from the client. A future improvement should derive the application owner from the JWT-authenticated user instead of accepting `userId` from request data.

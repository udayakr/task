# Task Management System (TMS)

Full-stack task management application with Spring Boot 3.2 REST API and React 18 frontend.

## Prerequisites

- Java 21, Maven 3.9+
- Node.js 20+
- Docker & Docker Compose

## Quick Start (Docker)

```bash
docker-compose up --build
# Frontend:  http://localhost:3000
# Backend:   http://localhost:8080
# Swagger:   http://localhost:8080/swagger-ui.html
```

## Local Development

```bash
# Start infra
docker-compose up postgres redis -d

# Backend
./mvnw spring-boot:run

# Frontend (new terminal)
cd frontend && npm install && npm run dev
# → http://localhost:5173
```

## Seed Accounts

| Email           | Password    | Role  |
|-----------------|-------------|-------|
| admin@tms.com   | Admin@1234  | ADMIN |
| alice@tms.com   | Admin@1234  | USER  |
| bob@tms.com     | Admin@1234  | USER  |

## Running Tests

```bash
./mvnw test                          # backend unit tests
./mvnw verify                        # with coverage report
cd frontend && npm test              # frontend unit tests
cd frontend && npm run test:coverage # coverage report
cd frontend && npm run test:e2e      # Playwright E2E
```

## Key Environment Variables

| Variable      | Default                              | Description         |
|---------------|--------------------------------------|---------------------|
| DB_URL        | jdbc:postgresql://localhost:5432/tms | PostgreSQL URL      |
| DB_USERNAME   | tms                                  | DB username         |
| DB_PASSWORD   | secret                               | DB password         |
| REDIS_HOST    | localhost                            | Redis host          |
| JWT_SECRET    | (base64 default)                     | 256-bit JWT secret  |
| MAIL_HOST     | smtp.mailtrap.io                     | SMTP server         |
| CORS_ORIGINS  | http://localhost:5173                | Allowed origins     |

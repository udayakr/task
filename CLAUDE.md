# CLAUDE.md — Task Management System (TMS)

## Project Overview

Full-stack Task Management System with a Spring Boot 3.2 REST API backend and a React 18 TypeScript frontend. The backend lives at the repository root; the frontend is under `./frontend/`.

- **Backend package**: `com.tms`
- **Backend port**: `8080`
- **Frontend port (dev)**: `5173` | **Frontend port (Docker)**: `3000`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

---

## Essential Commands

### Backend
```bash
./mvnw spring-boot:run          # run locally (requires postgres + redis running)
./mvnw test                     # unit tests
./mvnw verify                   # tests + JaCoCo coverage report
./mvnw package -DskipTests      # build JAR
```

### Frontend
```bash
cd frontend
npm install                     # install dependencies
npm run dev                     # dev server → http://localhost:5173
npm test                        # Vitest unit tests
npm run test:coverage           # unit tests + coverage
npm run test:e2e                # Playwright E2E tests
npm run build                   # production build
```

### Docker (full stack)
```bash
docker-compose up --build               # build and start all services
docker-compose up postgres redis -d     # start infra only (for local dev)
docker-compose down -v                  # stop and remove volumes
```

---

## Architecture

### Backend Layers
```
TmsApplication
└── controller/     → REST endpoints, delegates to service
└── service/        → Business logic, @Transactional
└── repository/     → Spring Data JPA, custom JPQL queries
└── model/          → JPA entities + enums
└── dto/            → Request/Response DTOs (never expose entities)
└── security/       → JWT filter, UserDetailsService
└── config/         → SecurityConfig, RedisConfig, SwaggerConfig, AppProperties
└── exception/      → Custom exceptions + GlobalExceptionHandler
└── aop/            → @Auditable annotation + AuditAspect
└── util/           → ScheduledTasks (daily email reminders)
```

### Frontend Structure
```
frontend/src/
└── api/            → RTK Query endpoint slices (authApi, projectApi, taskApi, userApi, dashboardApi)
└── app/            → Redux store (store.ts)
└── components/     → Shared UI (Navbar, Sidebar, KanbanBoard, TaskCard, LoadingSkeleton, EmptyState, ProtectedRoute)
└── features/       → Redux slices: authSlice (JWT state), uiSlice (dark mode, sidebar)
└── hooks/          → useAppDispatch / useAppSelector typed hooks
└── pages/          → Route-level pages (Login, Register, Dashboard, Projects, ProjectDetail, MyTasks, Profile, AdminUsers)
└── types/          → TypeScript interfaces (index.ts)
└── utils/          → axiosInstance (with refresh interceptor), cn() utility
└── test/           → Vitest unit tests + setup
frontend/e2e/       → Playwright E2E tests
```

---

## Key File Locations

| What | Path |
|---|---|
| Spring Security config | `src/main/java/com/tms/config/SecurityConfig.java` |
| JWT utility | `src/main/java/com/tms/security/JwtUtil.java` |
| JWT filter | `src/main/java/com/tms/security/JwtAuthenticationFilter.java` |
| App config properties | `src/main/java/com/tms/config/AppProperties.java` |
| Global exception handler | `src/main/java/com/tms/exception/GlobalExceptionHandler.java` |
| API response envelope | `src/main/java/com/tms/dto/response/ApiResponse.java` |
| DB schema (Flyway V1) | `src/main/resources/db/migration/V1__init.sql` |
| Seed data (Flyway V2) | `src/main/resources/db/migration/V2__seed.sql` |
| Application config | `src/main/resources/application.yml` |
| Email templates | `src/main/resources/templates/email/` |
| Redux store | `frontend/src/app/store.ts` |
| RTK Query base slice | `frontend/src/api/apiSlice.ts` |
| Axios instance + interceptor | `frontend/src/utils/axiosInstance.ts` |
| Route definitions | `frontend/src/App.tsx` |
| TypeScript types | `frontend/src/types/index.ts` |

---

## Architecture Decisions — Do Not Change

### Backend
- **Spring Boot 3.2.5** — do NOT upgrade to 4.x (breaking changes in security and auto-config)
- **PostgreSQL native enums** — all enums (`user_role`, `task_status`, etc.) are PostgreSQL native types mapped via `@JdbcTypeCode(SqlTypes.NAMED_ENUM)`. Do NOT switch to `VARCHAR`
- **Flyway manages schema** — `ddl-auto` is set to `validate`. Never use `create` or `create-drop` in non-test profiles
- **`ApiResponse<T>` envelope** — every endpoint returns `ApiResponse<T>`. Never return raw objects or `ResponseEntity` without wrapping
- **`PagedResponse<T>`** — all paginated lists return this wrapper (0-based page index)
- **JWT stateless** — access token 15 min, refresh token 7 days. Refresh tokens are stored in Redis and can be revoked via blacklist keys
- **BCrypt strength 12** — do not lower it

### Frontend
- **RTK Query for all API calls** — do NOT use `axiosInstance` directly in components/pages for data fetching; use generated hooks from `src/api/`
- **`useAppDispatch` / `useAppSelector`** — always use these typed hooks, never raw `useDispatch`/`useSelector`
- **`cn()` from `@/utils/cn.ts`** — always use for conditional Tailwind class merging
- **Auth state in Redux `authSlice`** — tokens also mirrored to `localStorage` for page refresh persistence
- **Dark mode** — toggled via `uiSlice.toggleDarkMode()`, persisted to `localStorage`, applied as `dark` class on `<html>`

---

## Caching (Redis)

| Cache Name | TTL | Evicted When |
|---|---|---|
| `userProfile` | 30 min | Profile update |
| `projectStats` | 5 min | Any task change in project |
| `dashboardSummary` | 2 min | Any task change for user |
| `taskCache` | 10 min | Task update or delete |

---

## Security Model

- `ROLE_ADMIN` — full access to all resources
- `ROLE_USER` — access only to projects they own or are a member of
- Task deletion requires: task creator OR assignee OR project owner
- Comment deletion requires: comment author OR admin
- Method-level security via `@PreAuthorize` on controller methods

---

## API Conventions

- All endpoints prefixed `/api/v1`
- Auth endpoints (`/api/v1/auth/**`) are public; everything else requires Bearer JWT
- Pagination: `page` (0-based), `size` (default 20, max 100), `sort` (`field,direction`)
- Validation errors → 400 with `details` map of `field → message`
- Standard error codes: `NOT_FOUND`, `CONFLICT`, `BAD_REQUEST`, `FORBIDDEN`, `UNAUTHORIZED`, `TOKEN_EXPIRED`, `VALIDATION_ERROR`, `INTERNAL_ERROR`

---

## Database

- **PostgreSQL 15** (host: `localhost:5432`, DB: `tms`, user: `tms`, password: `secret` for dev)
- **Redis 7** (host: `localhost:6379`) — used for token blacklist + Spring Cache
- New schema changes → add a new Flyway migration file `V3__description.sql` (never edit existing migrations)

---

## Seed Accounts

| Email | Password | Role |
|---|---|---|
| `admin@tms.com` | `Admin@1234` | ADMIN |
| `alice@tms.com` | `Admin@1234` | USER |
| `bob@tms.com` | `Admin@1234` | USER |

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/tms` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `tms` | DB username |
| `DB_PASSWORD` | `secret` | DB password |
| `REDIS_HOST` | `localhost` | Redis hostname |
| `REDIS_PORT` | `6379` | Redis port |
| `JWT_SECRET` | (base64 default in yml) | Min 256-bit secret — **change in production** |
| `MAIL_HOST` | `smtp.mailtrap.io` | SMTP server |
| `MAIL_PORT` | `587` | SMTP port |
| `MAIL_USERNAME` | — | SMTP username |
| `MAIL_PASSWORD` | — | SMTP password |
| `CORS_ORIGINS` | `http://localhost:5173` | Comma-separated allowed origins |
| `STORAGE_TYPE` | `local` | `local` or `s3` |
| `STORAGE_PATH` | `./uploads` | Local file storage path |
| `FRONTEND_URL` | `http://localhost:5173` | Used in email links |
| `VITE_API_BASE_URL` | `http://localhost:8080` | Frontend API base URL |

---

## Testing

### Backend
- Unit tests: JUnit 5 + Mockito in `src/test/java/com/tms/`
- Test profile: `application-test.yml` uses H2 in-memory DB, Flyway disabled
- Coverage target: ≥ 80% line coverage on services

### Frontend
- Unit tests: Vitest + Testing Library in `src/test/`
- E2E tests: Playwright in `frontend/e2e/`
- Coverage target: ≥ 75% line coverage

---

## Do NOT

- Do not commit secrets (`JWT_SECRET`, `MAIL_PASSWORD`, etc.) — use env vars
- Do not edit `V1__init.sql` or `V2__seed.sql` — add new `V3__*.sql` migrations instead
- Do not expose JPA entities directly in API responses — always use DTOs
- Do not add `@Transactional` to controllers — only services
- Do not use `axiosInstance` directly in React components for data fetching — use RTK Query hooks
- Do not bypass the `ProtectedRoute` component for authenticated pages

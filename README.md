# AgentOS — Vertical AI Revenue Infrastructure

AgentOS is a multi-tenant platform for deploying AI-assisted revenue workflows
(capture → qualify → route → follow-up) for high-value, operationally fragmented
industries. See [`AgentOS_Strategy.md`](./AgentOS_Strategy.md) for the full
product strategy and technical specification.

> **Status:** early foundation. Only the `core` module is implemented; the
> vertical modules, integration adapters, workflow engine, and AI orchestration
> described in the strategy are not yet built. See
> [Roadmap](#roadmap) below.

---

## What exists today

A Spring Boot 3.3.5 / Java 21 multi-tenant foundation with:

- **Authentication** — JWT-based login + tenant-bootstrap registration
  (`POST /api/auth/login`, `POST /api/auth/register`).
- **Multi-tenancy** — tenant-scoped data access enforced per request via the
  authenticated principal; every lead/opportunity read/write is filtered by
  `tenant_id`.
- **Revenue objects** — `Lead` and `Opportunity` CRUD (`/api/leads`,
  `/api/opportunities`).
- **Audit logging** — mutations (login, register, lead/opportunity create/delete)
  are recorded asynchronously via `AuditService`.
- **Database** — PostgreSQL via Flyway migrations (`V1__initial_schema.sql`)
  with an H2 test profile for CI.
- **Validation & error handling** — bean validation on request bodies and a
  global `@RestControllerAdvice` producing consistent JSON error envelopes.

### Repository layout (actual)

```
AgentOS/
├── README.md
├── AgentOS_Strategy.md          # product strategy (single source of truth)
├── build.gradle                  # root: Spring Boot + Java 21
├── settings.gradle
├── gradle.properties
├── docker-compose.yml            # local dev stack (Postgres + app)
├── .env.example
├── .github/workflows/ci.yml      # build + test
├── core/                         # the only implemented module
│   ├── build.gradle
│   └── src/
│       ├── main/java/com/agentos/core/
│       │   ├── AgentosApplication.java
│       │   ├── config/           # SecurityConfig, AsyncConfig
│       │   ├── controller/       # Auth, Lead, Opportunity, GlobalExceptionHandler
│       │   ├── dto/              # request/response DTOs with validation
│       │   ├── entity/           # Tenant, User, Lead, Opportunity, AuditLog
│       │   ├── repository/       # Spring Data JPA repositories
│       │   ├── security/         # JWT filter, JwtService, principal, user details
│       │   └── service/          # TenantService, UserService, AuditService
│       └── main/resources/
│           ├── application.yaml
│           └── db/migration/V1__initial_schema.sql
├── modules/                      # stubs: commercial-roofing, hvac, etc. (empty)
├── integration/adapters/         # stub: CRM/ERP/SMS/email adapters (empty)
├── infra/                         # stubs: auth, tenancy, authz, audit, billing (empty)
└── ui/                            # stubs: widgets, portal, worker (empty)
```

The `modules/`, `integration/`, `infra/`, and `ui/` directories currently contain
only empty `build.gradle` stubs — they are placeholders for the roadmap below,
not implemented code.

---

## Quick start

### Prerequisites

- Java 21
- Docker + Docker Compose (for the database), or a local PostgreSQL 15+

### Run locally

```bash
# 1. Copy environment config and set secrets
cp .env.example .env
# edit .env: set a strong JWT_SECRET and DB_PASSWORD

# 2. Start Postgres
docker compose up -d postgres

# 3. Build and run
./gradlew :core:bootRun
# API at http://localhost:8080
```

### Run the full stack via Docker Compose

```bash
cp .env.example .env
# edit .env
docker compose up --build
```

### Run tests

```bash
./gradlew test
```

Tests use an in-memory H2 database (profile `test`); no external services required.

---

## API reference

All endpoints except `/api/auth/**` require a `Authorization: Bearer <jwt>` header.

### Authentication

`POST /api/auth/register` — bootstrap a new tenant and its first admin.

```json
{
  "tenantName": "Example Roofing Co",
  "email": "owner@example.com",
  "password": "at-least-8-chars"
}
```

`POST /api/auth/login`

```json
{
  "email": "owner@example.com",
  "password": "at-least-8-chars"
}
```

Returns:

```json
{
  "accessToken": "eyJ...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "tenantId": "...",
  "userId": "...",
  "email": "owner@example.com",
  "role": "ADMIN"
}
```

### Leads

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/leads` | List leads for the caller's tenant |
| `POST` | `/api/leads` | Create a lead (tenant-scoped) |
| `GET` | `/api/leads/{id}` | Get a lead (must belong to caller's tenant) |
| `DELETE` | `/api/leads/{id}` | Delete a lead (must belong to caller's tenant) |

### Opportunities

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/opportunities` | List opportunities for the caller's tenant |
| `POST` | `/api/opportunities` | Create an opportunity (tenant-scoped) |
| `GET` | `/api/opportunities/{id}` | Get an opportunity (must belong to caller's tenant) |
| `DELETE` | `/api/opportunities/{id}` | Delete an opportunity (must belong to caller's tenant) |

---

## Roadmap

Aligned to the strategy's engineering phases (§54). Items in **bold** are
next priorities.

1. **Foundation (Phase 1)** — ✅ auth, multi-tenancy, audit, validation.
   Still needed: real RBAC service, tenant-existence enforcement on writes,
   tenant admin user-management endpoints.
2. **Revenue objects (Phase 2)** — partial (lead/opportunity CRUD). Still
   needed: contacts, activities, notes, qualification state machine.
3. **Intake (Phase 3)** — website widget, public intake endpoint, file uploads.
4. **AI orchestration (Phase 4)** — **the core differentiator**: versioned
   agent definitions, tool calling with explicit authorization, structured
   extraction, qualification assistance, human escalation.
5. **Workflow engine (Phase 5)** — triggers, conditions, actions, branching,
   delays, retries, human approval.
6. **Integrations (Phase 6)** — CRM (HubSpot/Salesforce), email, SMS, calendar,
   webhooks — all behind adapter interfaces.
7. **Verticalization (Phase 7)** — first vertical module (commercial roofing).
8. **White-label (Phase 8)** — reseller accounts, custom domains, branding.
9. **Scale (Phase 9)** — observability, billing, usage metering, background jobs.

See [`AgentOS_Strategy.md`](./AgentOS_Strategy.md) for the complete specification.

---

## Contributing

- All configuration in YAML; all data stored with `tenant_id`; all mutations
  audited; all AI behavior will be versioned.
- Run `./gradlew test` before pushing. CI runs the same.
- Do not add customer-specific logic to the core — see strategy §52/§53.

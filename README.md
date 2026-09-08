# AgentOS — Vertical AI Revenue Infrastructure

[![Release](https://img.shields.io/badge/release-1.0-blue)](https://github.com/geekhaus314/agentos)
[![License: MIT](https://img.shields.io/badge/license-MIT-green)](./LICENSE)

A production-grade, multi-tenant platform for vertical AI revenue workflows (qualifications, intake, orchestration, and audit-ready integrations).

Version: 1.0 — 2026-08-09

Quickstart
---------
Prerequisites: Docker & Docker Compose installed.

Start locally using Docker Compose:

```bash
# build and run services
docker-compose up --build
```

Open the platform at http://localhost:8080 (default) after services are healthy.

Developer setup
---------------
For local development and environment bootstrapping, see the scripts/ folder:

```bash
# prepare dev environment (secrets, DB migrations, local config)
./scripts/bootstrap.sh
```

Contributing, License, and Support
----------------------------------
Please read CONTRIBUTING.md (if present) and CODE_OF_CONDUCT.md. This repository is licensed under the MIT License. See LICENSE for details.

---

# 2. Repository Structure

```
AgentOS/
├── README.md
├── AGENTS.md
├── .gitignore
├── Dockerfile
├── docker-compose.yml
├── .opencode/
│   └── opencode.json
├── core/
│   ├── src/main/java/com/agentos/core/
│   │   ├── Application.java
│   │   ├── SecurityConfig.java
│   │   ├── Config.java
│   │   └── ...
│   ├── src/test/java/com/agentos/core/test/
│   └── resources/
│       ├── application.yaml
│       └── schema/
├── modules/
│   ├── commercial-roofing/
│   │   ├── src/main/java/com/agentos/modules/roofing/
│   │   │   ├── RoofingModule.java
│   │   │   ├── IntakeService.java
│   │   │   ├── QualificationEngine.java
│   │   │   └── WorkflowEngine.java
│   │   └── test/
│   ├── commercial-insurance/
│   │   └── ...
│   ├── hvac/
│   │   └── ...
│   ├── restoration/
│   │   └── ...
│   ├── industrial/
│   │   └── ...
│   └── common/
│       └── ...
├── integration/
│   ├── adapters/
│   │   ├── crm/
│   │   │   ├── HubSpotAdapter.java
│   │   │   ├── SalesforceAdapter.java
│   │   │   └── CRMAdapter.java (interface)
│   │   ├── erp/
│   │   │   ├── SAPAdapter.java
│   │   │   ├── OracleAdapter.java
│   │   │   └── ERPAdapter.java (interface)
│   │   ├── sms/
│   │   │   ├── TwilioAdapter.java
│   │   │   └── SMSAdapter.java (interface)
│   │   ├── email/
│   │   │   ├── SendGridAdapter.java
│   │   │   └── EmailAdapter.java (interface)
│   │   ├── calendar/
│   │   │   ├── GoogleCalendarAdapter.java
│   │   │   └── CalendarAdapter.java (interface)
│   │   ├── storage/
│   │   │   ├── S3Adapter.java
│   │   │   └── LocalStorageAdapter.java
│   │   └── webhook/
│   │       ├── WebhookHandler.java
│   │       └── WebhookAdapter.java (interface)
│   ├── models/
│   │   └── IntegrationAdapter.java (interface)
│   └── src/test/
├── infra/
│   ├── auth/
│   │   ├── SecurityConfig.java
│   │   ├── TokenService.java
│   │   └── test/
│   ├── tenancy/
│   │   ├── TenantManager.java
│   │   ├── TenantResolver.java
│   │   └── test/
│   ├── authz/
│   │   ├── RoleService.java
│   │   ├── PermissionService.java
│   │   └── test/
│   ├── audit/
│   │   ├── AuditLogger.java
│   │   └── test/
│   ├── billing/
│   │   ├── BillingService.java
│   │   ├── Subscription.java
│   │   └── test/
│   └── src/test/
├── ui/
│   ├── widgets/
│   │   ├── WebsiteWidget.java
│   │   ├── IntakeWidget.java
│   │   └── test/
│   ├── portal/
│   │   ├── AdminPortal.java
│   │   └── test/
│   └── worker/
│       ├── WorkerService.java
│       ├── MessageProcessor.java
│       └── test/
├── docs/
│   ├── api/
│   │   ├── API_V1.md
│   │   ├── API_V2.md
│   │   └── test/
│   ├── flows/
│   │   ├── commercial-roofing-flow.md
│   │   ├── insurance-flow.md
│   │   └── test/
│   └── patterns/
│       ├── adapter-pattern.md
│       ├── event-driven-pattern.md
│       └── test/
├── scripts/
│   ├── bootstrap.sh
│   ├── migrate.sh
│   └── deploy.sh
├── tests/
│   ├── unit/
│   ├── integration/
│   ├── e2e/
│   └── security/
└── README.md
```

---

# 3. Core Architecture

## 3.1 Technology Stack

- **Language:** Java 21
- **Database:** PostgreSQL
- **Message Queue:** RabbitMQ / Apache Kafka
- **Storage:** S3-compatible object storage
- **Auth:** OAuth 2.0 / OpenID Connect
- **AI:** OpenAI / Anthropic API (with fallback)
- **Email:** SendGrid / Amazon SES
- **SMS:** Twilio
- **Calendar:** Google Calendar API
- **CRM:** HubSpot / Salesforce adapters
- **CI/CD:** GitHub Actions

## 3.2 Core Principles

1. **Multi-Tenancy** — Every tenant gets their own schema + data isolation
2. **AuthZ by Default** — All operations require explicit authorization
3. **Audit Everything** — Every user action, AI inference, and integration call is logged
4. **Human in the Loop** — AI handles repeatable work; humans handle exceptions
5. **No Guesses** — Business-critical information is never invented by AI
6. **Structured Output** — All AI-generated outputs are validated against schema
7. **Tool-Based Agents** — Agents use explicit tool calls, not free-form prompting
8. **White-Label Ready** — Resellers deploy with their own branding, domain, and policies

## 3.3 Data Model (Core Entities)

```sql
CREATE TABLE tenants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand_name VARCHAR(255),
    domain VARCHAR(255),
    status VARCHAR(50) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    config JSONB
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    role VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE permissions (
    id UUID PRIMARY KEY,
    role_id UUID REFERENCES roles(id),
    resource VARCHAR(100),
    action VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE leads (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    source VARCHAR(100),
    status VARCHAR(50) DEFAULT 'new',
    score INTEGER DEFAULT 0,
    contact_name VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    company VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE opportunities (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    source VARCHAR(100),
    status VARCHAR(50) DEFAULT 'new',
    qualification_status VARCHAR(50) DEFAULT 'new',
    priority VARCHAR(20) DEFAULT 'medium',
    assigned_to UUID,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

---

# 4. Configuration Management

## 4.1 Configuration Hierarchy

```
Platform Defaults
       |
       v
Vertical Defaults
       |
       v
Reseller Defaults
       |
       v
Tenant Configuration
       |
       v
Workflow-specific Configuration
```

## 4.2 Example Configuration

```yaml
# platform-defaults.yaml
platform:
  name: "AgentOS"
  version: "1.0.0"
  features:
    ai_orchestration: enabled
    webhooks: enabled
    multi_tenancy: enabled
  audit:
    enabled: true
    retention_days: 365

# vertical-defaults.yaml
vertical:
  commercial_roofing:
    service_area:
      states:
        - "Missouri"
        - "Illinois"
        - "Tennessee"
    minimum_project_size_sqft: 50000
    qualification_rules:
      - rule: "check_service_area"
        condition: "location IN service_area"
        result: "qualified"
      - rule: "check_project_size"
        condition: "project_size >= minimum_project_size"
        result: "qualified"
    routing:
      commercial_projects:
        department: "commercial_sales"
      residential_projects:
        department: "residential_sales"
    AI:
      human_escalation: enabled
    integration:
      crm: enabled
      email: enabled
      sms: enabled
      calendar: enabled

# tenant-config.yaml
tenant:
  name: "Example Roofing Co"
  domain: "exampleroofing.com"
  vertical: "commercial_roofing"
  branding:
    name: "Example Roofing"
    logo: "https://cdn.example.com/logo.png"
    primary_color: "#1a365d"
    secondary_color: "#e53e3e"
```

---

# 5. AI Architecture

## 5.1 Agent Framework

### Agent Definition

```yaml
agent:
  id: commercial_roofing_intake
  vertical: commercial_roofing
  version: "1.0.0"
  purpose: "Qualify commercial roofing opportunities and collect structured data"
  scope:
    allowed_actions:
      - collect_contact_information
      - collect_project_information
      - collect_photos
      - collect_documents
      - schedule_appointment
      - send_notification
      - escalate_to_human
    forbidden_actions:
      - make_pricing_decisions
      - provide_legal_advice
      - access_other_tenants_data
      - override_qualification_rules
    output_schema:
      type: object
      fields:
        contact:
          type: object
          properties:
            name: string
            company: string
            email: string
            phone: string
        project:
          type: object
          properties:
            location: string
            type: string
            size_sqft: integer
            timeline: string
            budget: integer
        qualification:
          type: object
          properties:
            status: string
            score: integer
            missing_info: list
            disqualifiers: list
        next_action:
          type: string
          enum:
            - request_contact
            - schedule_inspection
            - request_photos
            - request_proposal
            - escalate_to_human
```

## 5.2 Tool Interface

```java
public interface Tool {
    String getName();
    String getDescription();
    Map<String, Object> getInputSchema();
    AuthRule getAuthRule();
    ValidationResult validate(Map<String, Object> input);
    Object execute(Map<String, Object> input);
    AuditLog recordExecution(AuditLog.Record record);
}
```

### Tool Examples

```java
// collect_contact_information
@Tool(name = "collect_contact_information")
class CollectContactInfoTool implements Tool {
    // collects name, company, email, phone from user input
    // validates format
    // stores in tenant-scoped database
}

// get_business_hours
@Tool(name = "get_business_hours")
class GetBusinessHoursTool implements Tool {
    // returns business hours for tenant's industry
    // respects tenant configuration
}

// create_lead
@Tool(name = "create_lead")
class CreateLeadTool implements Tool {
    // creates a lead record
    // requires tenant_id, source, contact info
    // returns lead ID
}

// schedule_appointment
@Tool(name = "schedule_appointment")
class ScheduleAppointmentTool implements Tool {
    // schedules appointment
    // requires tenant_id, agent_id, date, time
    // returns appointment ID
}

// send_email
@Tool(name = "send_email")
class SendEmailTool implements Tool {
    // sends email via configured provider
    // requires tenant_id, recipient, subject, body
    // returns success/failure
}

// send_sms
@Tool(name = "send_sms")
class SendSmsTool implements Tool {
    // sends SMS via configured provider
    // requires tenant_id, recipient, message
    // returns success/failure
}
```

---

# 6. Integration Adapters

## 6.1 Adapter Interface

```java
public interface IntegrationAdapter {
    String getName();
    Map<String, Object> getConfig();
    Optional<String> getTenantId();
    boolean isAvailable();
    Optional<String> getConnectionUrl();

    // CRUD operations
    Optional<Record> createRecord(Record record);
    Optional<Record> updateRecord(String id, Record record);
    Optional<Record> deleteRecord(String id);
    Optional<Record> getRecord(String id);
    List<Record> queryRecords(Criteria criteria);
}
```

## 6.2 CRM Adapter

```java
@Component
public class HubSpotAdapter implements IntegrationAdapter {
    private final HubSpotClient hubSpotClient;

    @Override
    public Optional<Record> createRecord(Record record) {
        // Convert to HubSpot CRM object
        // Create in HubSpot
        // Return record ID
    }

    @Override
    public Optional<Record> updateRecord(String id, Record record) {
        // Find record by ID
        // Update fields
        // Return updated record
    }

    // ... other methods
}
```

---

# 7. Document Processing

## 7.1 Document Pipeline

```
Upload
  |
  v
Virus Scanning (ClamAV)
  |
  v
Object Storage (S3-compatible)
  |
  v
Metadata Extraction (exif, xmp, id3)
  |
  v
OCR Processing (Tesseract / AWS Textract)
  |
  v
Structured Extraction (NLP models)
  |
  v
Validation (schema, confidence scores)
  |
  v
Workflow Routing
```

### Document Model

```java
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String tenantId;
    private String fileName;
    private String mimeType;
    private String storageKey;
    private DocumentType type; // PDF, IMAGE, OFFICE, OTHER
    private boolean isTrusted;
    private List<String> tags;
    private Map<String, Object> metadata;
    private Instant uploadedAt;
}
```

---

# 8. Workflow Engine

## 8.1 Workflow Definition

```yaml
workflow:
  id: commercial_roofing_intake
  name: "Commercial Roofing Intake"
  trigger:
    event: "opportunity.created"
    source: "website_intake"
  steps:
    - step: 1
      name: "Collect Contact Information"
      tool: "collect_contact_information"
      condition: "all_fields_present"
      action: "proceed"

    - step: 2
      name: "Collect Project Details"
      tool: "collect_project_information"
      condition: "contact_validated"
      action: "proceed"

    - step: 3
      name: "Qualify Opportunity"
      tool: "qualify_opportunity"
      condition: "project_size >= 50000 AND location IN service_area"
      action: "qualify"
      then:
        - step: 4
          name: "Assign Salesperson"
          tool: "assign_salesperson"
          condition: "quota_check_passed"
          action: "proceed"
        - step: 4
          name: "Notify Salesperson"
          tool: "send_notification"
          condition: "quota_check_passed"
          action: "proceed"

    - step: 5
      name: "Schedule Inspection"
      tool: "schedule_appointment"
      condition: "qualified AND time_between_9_5_5_and_17_0"
      action: "proceed"

    - step: 6
      name: "Create Opportunity"
      tool: "create_opportunity"
      condition: "inspection_scheduled"
      action: "proceed"

    - step: 7
      name: "Follow-up"
      tool: "send_notification"
      condition: "opportunity_closed"
      action: "proceed"

  status_transitions:
    new → contact → qualify → assign → schedule → qualify → follow_up → closed
```

---

# 9. Security

## 9.1 Tenant Isolation

```java
@Service
public class TenantSecurityService {

    // Validate that all database queries are scoped to tenant
    public void validateTenantScopedQuery(String query, String expectedTenantId) {
        // Ensure query includes tenant_id filter
        // Reject queries without tenant_id
    }

    // Validate that all data accesses are scoped
    public void validateDataAccess(String resourceType, String resourceId, String expectedTenantId) {
        // Check if user has access to the resource
        // Reject if cross-tenant access
    }

    // Validate file uploads are scoped to tenant
    public void validateUpload(String tenantId, String fileName) {
        // Check upload is for this tenant's storage
        // Reject if cross-tenant
    }
}
```

---

# 10. Deployment

## 10.1 Docker Compose (Development)

```yaml
version: "3.8"
services:
  agentos:
    image: agentos/core:latest
    restart: unless-stopped
    environment:
      - TENANT_DB_HOST=postgres
      - AI_API_KEY=${AI_API_KEY}
      - SMTP_HOST=smtp.sendgrid.net
      - SMTP_PORT=587
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
      - rabbitmq

  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: agentos
      POSTGRES_USER: agentos
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    volumes:
      - redis_data:/data

  rabbitmq:
    image: rabbitmq:3.12-management
    ports:
      - "5672:5672"
      - "15672:15672"

  minio:
    image: minio/minio:latest
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
      MINIO_ROOT_USER: minio
      MINIO_ROOT_PASSWORD: minio123
    volumes:
      - minio_data:/minio
    ports:
      - "9000:9000"

volumes:
  postgres_data:
  redis_data:
  minio_data:
```

## 10.2 Docker Compose (Production)

```yaml
version: "3.8"
services:
  agentos:
    image: agentos/core:latest
    restart: unless-stopped
    environment:
      - TENANT_DB_HOST=postgres-prod
      - AI_API_KEY=${AI_API_KEY}
      - SMTP_HOST=smtp.sendgrid.net
      - SMTP_PORT=587
      - ENVIRONMENT=production
      - LOG_LEVEL=INFO
    ports:
      - "8080:8080"
    deploy:
      replicas: 3
      resources:
        limits:
          cpus: "2.0"
          memory: "4G"
    depends_on:
      - postgres-prod
      - redis-prod
      - rabbitmq-prod
      - minio-prod

  postgres-prod:
    image: postgres:15
    environment:
      POSTGRES_DB: agentos
      POSTGRES_USER: agentos
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres-prod_data:/var/lib/postgresql/data

  redis-prod:
    image: redis:7-alpine
    volumes:
      - redis-prod_data:/data

  rabbitmq-prod:
    image: rabbitmq:3.12-management
    ports:
      - "5672:5672"
      - "15672:15672"

  minio-prod:
    image: minio/minio:latest
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
      MINIO_ROOT_USER: minio
      MINIO_ROOT_PASSWORD: minio123
    volumes:
      - minio-prod_data:/minio
    ports:
      - "9000:9000"

volumes:
  postgres-prod_data:
  redis-prod_data:
  minio-prod_data:
```

---

# 11. Monitoring & Observability

## 11.1 Metrics

```java
@Aspect
@Aspect("a:metrics")
public class MetricsAspect {

    private MeterRegistry meterRegistry;

    @Around("execution(* com.agentos.core..*(..))")
    public Object recordMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        String operation = joinPoint.getSignature().getName();
        long start = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
            meterRegistry.counter("operation.duration", operation)
                .tag("tenant", getTenantId())
                .tag("status", "success")
                .record();
            return result;
        } catch (Exception e) {
            meterRegistry.counter("operation.duration", operation)
                .tag("tenant", getTenantId())
                .tag("status", "error")
                .record();
            throw e;
        }
    }
}
```

## 11.2 Logging

```yaml
# logging.yml
logging:
  level:
    org.agentos: INFO
    org.agentos.core: INFO
    org.agentos.agent: DEBUG
  format: json
  output:
    console: true
    file: /var/log/agentos/application.log
    rotation: daily
```

---

# 12. Quick Start

## 12.1 Clone & Build

```bash
git clone https://github.com/agentos/agentos.git
cd agentos

# Environment setup
cp .env.example .env
# Edit .env with your values

# Build
./gradlew build -x test

# Run dev server
./gradlew dev --no-daemon
# Or: ./gradlew dev --env=development
```

## 12.2 Run Development Server

```bash
./gradlew dev
# Access at http://localhost:8080
```

## 12.3 Run Tests

```bash
./gradlew test
```

## 12.4 Deploy to Docker

```bash
docker-compose -f docker-compose.dev.yml up -d

# Production
docker-compose -f docker-compose.prod.yml up -d
```

---

# 13. API Reference

## 13.1 Core Endpoints

### Authentication

**POST /auth/login**

```json
{
  "email": "user@example.com",
  "password": "secure_password",
  "tenant_id": "tenant-uuid-here"
}
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "tenant_id": "tenant-uuid-here",
  "user": {
    "id": "user-uuid-here",
    "email": "user@example.com",
    "role": "admin",
    "organization": "Example Roofing"
  }
}
```

### Leads

**GET /api/leads**

```json
{
  "status": "new",
  "score": 50,
  "count": 12,
  "next": 21
}
```

**POST /api/leads**

```json
{
  "source": "website",
  "contact_name": "John Smith",
  "contact_email": "john@example.com",
  "contact_phone": "555-123-4567",
  "company": "Example Roofing Inc",
  "location": "St. Louis, MO"
}
```

### Opportunities

**GET /api/opps**

**POST /api/opps**

---

# 14. Appendix

## 14.1 Glossary

| Term | Definition |
|------|------------|
| Tenant | Isolated environment for one customer |
| Agent | AI processor that handles lead qualification and workflow |
| Integration Adapter | Connector between AgentOS and third-party systems |
| Vertical | Industry-specific workflow module |
| White-label | Deployment where the reseller's brand is visible |
| Qualification | Process of evaluating a lead/opportunity against rules |
| Lead Score | Numeric value representing lead quality |

## 14.2 Security Checklist

- [ ] All API requests are authenticated
- [ ] All API requests have appropriate permissions
- [ ] Tenant isolation is enforced at database level
- [ ] File uploads are scanned for malware
- [ ] All sensitive data is encrypted at rest
- [ ] All API calls are logged
- [ ] Rate limiting is applied
- [ ] Webhook signatures are verified
- [ ] AI outputs are validated against schema
- [ ] Cross-tenant data access is prevented
- [ ] All secrets are stored in vault, not in code

## 14.3 Development Notes

- All configuration should be in YAML
- All API calls should be type-safe
- All integrations should use adapters
- All data should be stored with tenant_id
- All operations should be audited
- All AI behavior should be versioned

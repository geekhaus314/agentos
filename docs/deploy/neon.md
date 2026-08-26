Neon Postgres notes

- Create a Neon project and database for AgentOS.
- Use a connection string with sslmode=require. Store it in Render as DATABASE_URL.
- Prefer Neon branching for safe migrations and testing.
- Configure pooling: Neon provides connection pooling; for serverful apps use a pooled connection or PgBouncer when needed.
- Ensure credentials are rotated and stored in a secrets manager.

Migration guidance
- Use Flyway or Liquibase (V1__initial_schema.sql present). Run migrations during CI or as a startup job.

Cost & scaling
- Start with a small plan for dev; scale as needed. Use read replicas for heavy read workloads.
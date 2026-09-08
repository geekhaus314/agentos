Render + Cloudflare + Neon deployment guide

Overview
- Backend: Render web service using Docker (core/Dockerfile)
- Database: Neon Postgres (managed) with connection string in Render secrets
- Frontend: Cloudflare Pages (static assets) or Cloudflare CDN in front of backend for edge caching

Steps (backend)
1. Push branch to GitHub.
2. On Render, create a new Web Service: connect the repo, set env to Docker, Dockerfile path core/Dockerfile.
3. Add environment variable DATABASE_URL with Neon connection string and set SPRING_PROFILES_ACTIVE=production.
4. Configure health check: /actuator/health and set appropriate health check path.
5. Create background worker service if needed for agents/workers.

Steps (database)
1. Create a Neon project and database.
2. Create a role/user and a connection string with sslmode=require.
3. Configure pooling (Neon pooling or PgBouncer) and set connection limits.

Steps (frontend)
- If UI can produce static assets (recommended), configure Cloudflare Pages to build from the ui/ folder. If the UI is currently Spring-based, consider building a simple static frontend or host the UI as a Render service behind Cloudflare.
- Configure Cloudflare Pages to point to the repository; set build command and publish directory.
- Add a custom domain and enable automatic TLS.

Notes & Checklist
- Use a secrets manager (Render dashboard secret envs) for DB credentials, API keys.
- Enable private networking in Render between services where possible.
- Add CI workflow to build and run unit tests before deploy.

Rollback
- Render keeps deploy history — use previous revision to rollback quickly.

Contact
- Provide Render/Cloudflare project names and domains and I will generate exact YAML/Pages config and a GitHub Actions workflow.
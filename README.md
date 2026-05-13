# symptom-witch

MVP foundation for Symptom Witch:

- `api`: Kotlin + Spring Boot + Flyway + PostgreSQL
- `web`: Vite + React + TypeScript + TanStack Router/Form/Query + shadcn/ui
- `openapi/openapi.yaml`: internal API contract stub

## Local setup

1. Start PostgreSQL:
   - `docker compose up -d`
2. Configure API environment:
   - `cd api && cp .env.example .env`
   - Before running the API locally: `set -a; source .env; set +a`
3. Run API tests:
   - `cd api && ./gradlew test`
4. Run web checks:
   - `cd web && cp .env.example .env && npm install && npm run lint && npm run test && npm run build`

## Task runners and debug targets

With Cursor/VS Code, use **Run Task**:

- `db:up` / `db:down`: start or stop PostgreSQL
- `api:debug`: starts PostgreSQL, then starts the API with JVM debug attach on port `5005`
- `web:dev`: starts the Vite app at `http://127.0.0.1:5173/`

With **Run and Debug**:

- `Debug API`: starts `api:debug`, then attaches to `127.0.0.1:5005`
- `Debug Web`: launches Chrome against `http://127.0.0.1:5173/`
- `Debug Full Stack`: starts both debug targets together

## Lint and format

- API lint: `cd api && ./gradlew lint`
- API format: `cd api && ./gradlew format`
- Web lint (includes typecheck): `cd web && npm run lint`
- Web format: `cd web && npm run format`
- Web format check: `cd web && npm run format:check`

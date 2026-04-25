# symptom-witch

MVP foundation for Symptom Witch:

- `api`: Kotlin + Spring Boot + Flyway + PostgreSQL
- `web`: Vite + React + TypeScript + TanStack Router/Form/Query + shadcn/ui
- `openapi/openapi.yaml`: internal API contract stub

## Local setup

1. Start PostgreSQL:
   - `docker compose up -d`
2. Run API tests:
   - `cd api && ./gradlew test`
3. Run web checks:
   - `cd web && npm install && npm run lint && npm run test && npm run build`

## Lint and format

- API lint: `cd api && ./gradlew lint`
- API format: `cd api && ./gradlew format`
- Web lint (includes typecheck): `cd web && npm run lint`
- Web format: `cd web && npm run format`
- Web format check: `cd web && npm run format:check`

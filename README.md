# symptom-witch

MVP foundation for Symptom Witch:

- `api`: Kotlin + Spring Boot + Flyway + PostgreSQL
- `web`: Vite + React + TypeScript + TanStack Router/Form/Query + shadcn/ui
- `openapi/openapi.yaml`: internal API contract stub

## Local setup

Prerequisites: `just`, `overmind`, Docker.

1. Configure API environment: `cd api && cp .env.example .env`
2. Configure web environment: `cd web && cp .env.example .env && npm install`
3. Start everything: `just dev`

`just dev` uses overmind to start PostgreSQL, the Spring Boot API (with JVM debug on port `5005`), and the Vite web app together. Logs are labeled per process; Ctrl+C cleanly shuts all three down.

## Common tasks

| Command                       | Description                         |
| ----------------------------- | ----------------------------------- |
| `just dev`                    | Start all services (db + api + web) |
| `just db-up` / `just db-down` | Start or stop PostgreSQL only       |
| `just api-dev`                | Run API standalone                  |
| `just web-dev`                | Run web app standalone              |
| `just test`                   | Run all tests                       |
| `just lint`                   | Lint all                            |
| `just format`                 | Format all                          |

## Debugging in VS Code

Run `just dev` first, then use **Run and Debug**:

- `Debug API`: attaches to `127.0.0.1:5005`
- `Debug Web`: launches Chrome against `http://127.0.0.1:5173/`
- `Debug Full Stack`: attaches both together

## Lint and format

- API lint: `cd api && ./gradlew lint`
- API format: `cd api && ./gradlew format`
- Web lint (includes typecheck): `cd web && npm run lint`
- Web format: `cd web && npm run format`
- Web format check: `cd web && npm run format:check`

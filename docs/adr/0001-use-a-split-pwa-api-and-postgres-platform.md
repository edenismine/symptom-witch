# Use a split PWA, API, and Postgres platform

Symptom Witch is built as a Vite React PWA, a Kotlin/Spring Boot API, and a PostgreSQL database managed by Flyway migrations. This keeps the browser experience installable and offline-capable while giving sensitive product data a server-owned persistence boundary and a conventional migration path.

## Considered Options

- Single full-stack JavaScript app with one deployment unit.
- Split PWA plus JVM API backed by Postgres.

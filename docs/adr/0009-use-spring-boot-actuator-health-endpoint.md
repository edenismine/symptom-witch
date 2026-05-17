# Use Spring Boot Actuator for the health endpoint

## Status

Accepted

## Context

The API needs an unauthenticated health endpoint for infrastructure probes. The initial implementation used a hand-rolled `HealthController` returning `{ "status": "ok" }`. The `spring-boot-starter-actuator` dependency was already present in `build.gradle.kts`, meaning Boot's built-in `/actuator/health` endpoint was available but unused.

The hand-rolled controller is strictly worse: it is blind to DB liveness, adds a class that earns nothing, and will silently report healthy during a Flyway or connection-pool failure.

## Decision

Delete `HealthController` and expose `/actuator/health` via Spring Boot Actuator with `show-details: never`. `SecurityConfig` permits `/actuator/health` without authentication. All other actuator endpoints remain unexposed.

`show-details: never` satisfies ADR-0006 — no operational internals (DB connection strings, pool stats) are surfaced to unauthenticated callers.

## Consequences

- `/actuator/health` returns `{ "status": "UP" }` when the DB is reachable, `{ "status": "DOWN" }` otherwise — infrastructure probes get accurate liveness without extra code.
- One less class to maintain.
- The health endpoint path changes from `/health` to `/actuator/health` — any existing probe configuration must be updated.

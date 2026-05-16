# Result pattern for error handling

The API uses a sealed `ApiResult<T>` return type in services, a sealed `DomainError` hierarchy for domain error cases, and a single `@ControllerAdvice` that maps each `DomainError` variant to an RFC 9457 `ProblemDetail` response (`application/problem+json`). Spring Boot 4's native `ProblemDetail` support is used; no additional library is needed. The web uses TanStack Query's `error` field typed as `ApiError` — a two-variant discriminated union (`{ kind: "network" } | { kind: "api"; status: number; type: string; detail: string }`) — rather than wrapping queries in `neverthrow`, because TanStack Query already manages async error state and TypeScript does not enforce `Result` exhaustiveness the way Kotlin `when` does.

## Considered Options

- Throw exceptions from services and catch at the advice boundary — rejected because error paths are invisible in service signatures.
- `neverthrow` on the web — rejected because it adds wrapping ceremony on top of TanStack Query without compiler-enforced exhaustiveness in TypeScript.

## Consequences

- `DomainError` starts as a single flat sealed class. As features grow it should be split into per-feature sealed subtypes (e.g. `SymptomError`, `SymptomLogError`) under a common parent, keeping the `@ControllerAdvice` exhaustive match viable.
- Every new domain error case must be added to `DomainError` and handled in the `@ControllerAdvice` before it can reach HTTP.
- Web fetchers must catch network failures and fold them into `ApiError { kind: "network" }` at the fetch boundary so callers always see a typed `ApiError`, never a raw exception.

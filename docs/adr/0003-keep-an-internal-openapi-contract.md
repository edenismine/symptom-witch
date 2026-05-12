# Keep an internal OpenAPI contract

The API surface is documented in `openapi/openapi.yaml` as the internal Symptom Witch API contract, and CI validates that contract with Redocly. The contract is not yet generated from Spring handlers, so implementation changes that affect API behavior must update the OpenAPI file in the same slice to avoid drift.

## Consequences

- Breaking API behavior should be treated as a contract change, even before public API consumers exist.
- The `/health` contract should be reconciled with runtime behavior when auth endpoints are added; it is currently unauthenticated in code while the spec also lists a 401 response.

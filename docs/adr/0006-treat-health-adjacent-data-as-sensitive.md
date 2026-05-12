# Treat health-adjacent data as sensitive

Symptom logs, notes, preferences, and identity-linked records are treated as sensitive health-adjacent data even though the MVP does not provide diagnosis, treatment recommendations, or clinical workflows. The product favors data minimization, no client-side telemetry, operational logs without symptom or note payloads, and account deletion that hard-deletes user-owned product data and the linked Auth0 user after typed confirmation.

## Consequences

- Production go-live requires explicit review of privacy copy, retention expectations, logging policy, Auth0 settings, and hosting/database region.
- Application logs and metrics may describe operational health, but must not include symptom names, notes, or other sensitive payloads.
- Account deletion must be idempotent and retry-safe because it spans product data and Auth0 identity deletion.

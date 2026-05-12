# Make symptom logging offline-first and idempotent

Symptom logging must persist locally before the network round trip, then sync to the API with explicit pending, synced, and error states. Create requests need an idempotency key or equivalent server guarantee so retries and flaky reconnection do not duplicate symptom logs.

## Consequences

- The client owns an offline queue for pending mutations.
- The API owns deduplication for retried creates.
- Sync resilience must include backoff, queue depth guardrails, and user-facing recovery states rather than silent retry loops.

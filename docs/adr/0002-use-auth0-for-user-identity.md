# Use Auth0 for user identity

Symptom Witch uses Auth0 as the external identity provider: the PWA signs users in with Authorization Code plus PKCE and refresh token rotation, while the API model links product data to the Auth0 subject through `app_user.auth0_sub`. Protected API routes must validate Auth0-issued JWTs before accessing user-owned records, and account deletion must include deletion of the Auth0 user as well as product data.

## Consequences

- Auth0 tenant configuration is part of go-live readiness.
- The API must treat the Auth0 subject as the stable external identity key, not as product data owned by Symptom Witch.
- Local development and CI need seams for testing authenticated behavior without real production credentials.

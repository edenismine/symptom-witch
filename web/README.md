# Symptom Witch Web

Frontend for the Symptom Witch public landing and authenticated shell.

## Auth0 configuration

1. Copy `.env.example` to `.env`:
   - `cp .env.example .env`
2. Fill in Auth0 SPA values:
   - `VITE_AUTH0_DOMAIN`
   - `VITE_AUTH0_CLIENT_ID`
   - `VITE_AUTH0_AUDIENCE` matching the Auth0 API identifier used by the backend
3. In the Auth0 application settings, configure:
   - Allowed Callback URLs: `http://localhost:5173`
   - Allowed Logout URLs: `http://localhost:5173`
   - Allowed Web Origins: `http://localhost:5173`
4. Enable refresh token rotation for the SPA in Auth0.

## Scripts

- `npm run dev`: run local dev server
- `npm run test`: run unit tests
- `npm run lint`: run ESLint + typecheck
- `npm run build`: build production bundle

## Manual auth verification checklist

- Logged out: `/` shows landing copy, `Log in`, `Privacy`, and `Terms`
- `Log in` sends user through Auth0 and returns to `/` authenticated view
- Authenticated: `/` shows shell with symptom draft form and `Sign out`
- `Sign out` returns user to logged-out landing state

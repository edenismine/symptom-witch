# Start all services (db + api + web) via overmind
dev:
    overmind start

# Database
db-up:
    docker compose up -d postgres

db-down:
    docker compose down

# API (standalone)
api-dev:
    bash -lc 'set -a; source api/.env; set +a; cd api && ./gradlew bootRun --debug-jvm'

# Web (standalone)
web-dev:
    cd web && npm run dev -- --host 127.0.0.1

# Tests
test:
    cd api && ./gradlew test
    cd web && npm test

# Lint
lint:
    cd api && ./gradlew lint
    cd web && npm run lint

# Format
format:
    cd api && ./gradlew format
    cd web && npm run format

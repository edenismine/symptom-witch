db:  docker compose up postgres
api: bash -lc 'set -a; source api/.env; set +a; cd api && ./gradlew bootRun --debug-jvm'
web: bash -lc 'cd web && npm run dev -- --host 127.0.0.1'

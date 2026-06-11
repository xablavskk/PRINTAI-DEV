#!/usr/bin/env bash
# Sobe o MySQL via Docker, o backend (Spring Boot, porta 8080) e o frontend (Vite, porta 5173).
# Requisitos: Docker Desktop, JDK 21 e Node.js instalados.
set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cleanup() {
  echo ""
  echo "Encerrando..."
  [ -n "$BACKEND_PID" ] && kill "$BACKEND_PID" 2>/dev/null
  [ -n "$FRONTEND_PID" ] && kill "$FRONTEND_PID" 2>/dev/null
}
trap cleanup EXIT INT TERM

echo "==> Subindo banco de dados MySQL via Docker..."
cd "$ROOT_DIR"
docker compose up -d db

echo "==> Aguardando o MySQL ficar pronto..."
until [ "$(docker inspect -f '{{.State.Health.Status}}' printai-db 2>/dev/null)" = "healthy" ]; do
  sleep 2
done
echo "    MySQL pronto."

echo "==> Iniciando backend (Spring Boot) em http://localhost:8080 ..."
cd "$ROOT_DIR/api"
chmod +x mvnw
SPRING_PROFILES_ACTIVE=local DB_HOST=localhost DB_NAME=printai DB_USER=root DB_PASS=root123 ./mvnw -q spring-boot:run &
BACKEND_PID=$!

echo "==> Instalando dependências do frontend (se necessário)..."
cd "$ROOT_DIR/frontend"
if [ ! -d node_modules ]; then
  npm install
fi

echo "==> Iniciando frontend (Vite) em http://localhost:5173 ..."
npm run dev &
FRONTEND_PID=$!

wait

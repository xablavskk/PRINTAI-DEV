#!/usr/bin/env bash
# Sobe o PrintAI completo (MySQL, backend Spring Boot e frontend React) via Docker.
# Requisito: apenas o Docker Desktop instalado e em execução.
set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

echo "==> Construindo e subindo os containers (banco, backend e frontend)..."
docker compose up -d --build

echo ""
echo "PrintAI iniciado! (pode levar até 1 minuto na primeira vez, enquanto as imagens são construídas)"
echo "  Acesse: http://localhost:5173"
echo ""
echo "Ver logs:  docker compose logs -f"
echo "Encerrar:  docker compose down"

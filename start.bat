@echo off
REM Sobe o PrintAI completo (MySQL, backend Spring Boot e frontend React) via Docker.
REM Requisito: apenas o Docker Desktop instalado e em execucao.

set ROOT_DIR=%~dp0
cd /d "%ROOT_DIR%"

echo ==> Construindo e subindo os containers (banco, backend e frontend)...
docker compose up -d --build

echo.
echo PrintAI iniciado! (pode levar ate 1 minuto na primeira vez, enquanto as imagens sao construidas)
echo   Acesse: http://localhost:5173
echo.
echo Ver logs:  docker compose logs -f
echo Encerrar:  docker compose down
pause

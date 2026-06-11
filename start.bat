@echo off
REM Sobe o MySQL via Docker, o backend (Spring Boot, porta 8080) e o frontend (Vite, porta 5173).
REM Requisitos: Docker Desktop, JDK 21 e Node.js instalados.

set ROOT_DIR=%~dp0

echo ==> Subindo banco de dados MySQL via Docker...
cd /d "%ROOT_DIR%"
docker compose up -d db

echo ==> Aguardando o MySQL ficar pronto...
:waitdb
docker inspect -f "{{.State.Health.Status}}" printai-db | findstr "healthy" >nul
if errorlevel 1 (
  timeout /t 2 >nul
  goto waitdb
)
echo     MySQL pronto.

echo ==> Iniciando backend (Spring Boot) em http://localhost:8080 ...
start "PrintAI - Backend" cmd /k "cd /d "%ROOT_DIR%api" && set SPRING_PROFILES_ACTIVE=local && set DB_HOST=localhost && set DB_NAME=printai && set DB_USER=root && set DB_PASS=root123 && mvnw.cmd spring-boot:run"

echo ==> Instalando dependencias do frontend (se necessario)...
cd /d "%ROOT_DIR%frontend"
if not exist node_modules (
  call npm install
)

echo ==> Iniciando frontend (Vite) em http://localhost:5173 ...
start "PrintAI - Frontend" cmd /k "cd /d "%ROOT_DIR%frontend" && npm run dev"

echo.
echo Backend e frontend iniciados em janelas separadas.
echo Acesse http://localhost:5173
pause

@echo off
REM Java CMS - Deployment Script (Windows)
REM Usage: deploy.cmd [frontend|backend]

setlocal enabledelayedexpansion

set PROJECT_ROOT=%~dp0..
set FRONTEND_DIR=%PROJECT_ROOT%\java-frontend
set BACKEND_DIR=%PROJECT_ROOT%\java-backend

echo ========================================
echo   Java CMS Deployment Script
echo ========================================

if "%1"=="frontend" goto frontend
if "%1"=="backend" goto backend
goto both

:frontend
echo.
echo [Frontend] Starting deployment...
cd /d "%FRONTEND_DIR%"

if not exist "node_modules" (
    echo [Frontend] Installing dependencies...
    call npm install
)

echo [Frontend] Building production version...
call npm run build

echo [Frontend] Starting server on port 3000...
start /b cmd /c "npm run serve"
echo [Frontend] Server started!
goto end

:backend
echo.
echo [Backend] Starting deployment...
cd /d "%BACKEND_DIR%"

echo [Backend] Building JAR package...
call mvn clean package -DskipTests

echo [Backend] Starting Spring Boot on port 8080...
start /b cmd /c "java -jar target\java-backend-0.0.1-SNAPSHOT.jar"
echo [Backend] Server started!
goto end

:both
call :frontend
call :backend
goto end

:end
echo.
echo ========================================
echo   Deployment Complete!
echo ========================================
echo   Frontend: http://localhost:3000
echo   Backend:  http://localhost:8080
echo ========================================
endlocal

@echo off
REM Java CMS Frontend - Deployment Script (Windows)

setlocal
cd /d "%~dp0"

echo ========================================
echo   Java CMS Frontend Deployment
echo ========================================

if not exist "node_modules" (
    echo [Info] Installing dependencies...
    call npm install
)

echo [Info] Building production version...
call npm run build

echo [Info] Starting server on port 3000...
call npm run serve

echo.
echo ========================================
echo   Frontend running at: http://localhost:3000
echo ========================================
endlocal

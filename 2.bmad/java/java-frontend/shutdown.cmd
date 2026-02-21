@echo off
REM Java CMS Frontend - Shutdown Script (Windows)

echo ========================================
echo   Java CMS Frontend Shutdown
echo ========================================

REM Find and kill process on port 3000
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3000 ^| findstr LISTENING') do (
    echo [Info] Found process %%a, terminating...
    taskkill /PID %%a /F >nul 2>&1
    echo [Info] Server stopped.
)

if errorlevel 1 (
    echo [Info] No process found on port 3000.
)

echo ========================================
echo   Shutdown Complete!
echo ========================================

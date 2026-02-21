@echo off
REM Java CMS - Shutdown Script (Windows)
REM Usage: shutdown.cmd [frontend|backend]

echo ========================================
echo   Java CMS Shutdown Script
echo ========================================

if "%1"=="frontend" goto frontend
if "%1"=="backend" goto backend
goto both

:frontend
echo.
echo [Frontend] Stopping server...

REM Find and kill process on port 3000
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3000 ^| findstr LISTENING') do (
    echo [Frontend] Found process %%a, terminating...
    taskkill /PID %%a /F >nul 2>&1
    echo [Frontend] Server stopped.
)
goto end

:backend
echo.
echo [Backend] Stopping server...

REM Find and kill process on port 8080
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    echo [Backend] Found process %%a, terminating...
    taskkill /PID %%a /F >nul 2>&1
    echo [Backend] Server stopped.
)
goto end

:both
call :frontend
call :backend
goto end

:end
echo.
echo ========================================
echo   Shutdown Complete!
echo ========================================

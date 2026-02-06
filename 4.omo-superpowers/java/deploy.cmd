@echo off
REM CMS Deployment Script for Windows
REM Usage: deploy.cmd [both|frontend|backend]

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set BACKEND_DIR=%SCRIPT_DIR%backend
set FRONTEND_DIR=%SCRIPT_DIR%frontend
set PID_DIR=%SCRIPT_DIR%.pids

REM Create PID directory
if not exist "%PID_DIR%" mkdir "%PID_DIR%"

set MODE=%1
if "%MODE%"=="" set MODE=both

if "%MODE%"=="both" goto :start_both
if "%MODE%"=="backend" goto :start_backend_only
if "%MODE%"=="frontend" goto :start_frontend_only
goto :usage

:start_both
call :start_backend
call :start_frontend
call :print_urls_both
goto :end

:start_backend_only
call :start_backend
call :print_urls_backend
goto :end

:start_frontend_only
call :start_frontend
call :print_urls_frontend
goto :end

:start_backend
echo [INFO] Starting backend server...

REM Check if Maven is available
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven not found. Please install Maven first.
    exit /b 1
)

cd /d "%BACKEND_DIR%"

echo [INFO] Building backend with Maven...
call mvn clean compile -q

echo [INFO] Starting Spring Boot application on port 8080...
start /b cmd /c "mvn spring-boot:run -q > %SCRIPT_DIR%backend.log 2>&1"

echo [INFO] Backend starting... Check backend.log for status
exit /b 0

:start_frontend
echo [INFO] Starting frontend server...

REM Check if npm is available
where npm >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] npm not found. Please install Node.js first.
    exit /b 1
)

cd /d "%FRONTEND_DIR%"

REM Install dependencies if needed
if not exist "node_modules" (
    echo [INFO] Installing frontend dependencies...
    call npm install
)

echo [INFO] Starting Vite dev server on port 3000...
start /b cmd /c "npm run dev > %SCRIPT_DIR%frontend.log 2>&1"

echo [INFO] Frontend starting... Check frontend.log for status
exit /b 0

:print_urls_both
echo.
echo =========================================
echo   CMS Application Started!
echo =========================================
echo   Public Site:  http://localhost:3000
echo   Admin Panel:  http://localhost:3000/admin
echo   API Base:     http://localhost:8080/api
echo =========================================
echo.
goto :eof

:print_urls_backend
echo.
echo =========================================
echo   Backend Started!
echo =========================================
echo   API Base: http://localhost:8080/api
echo =========================================
echo.
goto :eof

:print_urls_frontend
echo.
echo =========================================
echo   Frontend Started!
echo =========================================
echo   Public Site: http://localhost:3000
echo   Admin Panel: http://localhost:3000/admin
echo =========================================
echo.
goto :eof

:usage
echo Usage: %~nx0 [both^|frontend^|backend]
echo   both     - Start both frontend and backend (default)
echo   frontend - Start only the frontend
echo   backend  - Start only the backend
exit /b 1

:end
endlocal

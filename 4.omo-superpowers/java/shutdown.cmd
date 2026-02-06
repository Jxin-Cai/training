@echo off
REM CMS Shutdown Script for Windows
REM Usage: shutdown.cmd [both|frontend|backend]

setlocal enabledelayedexpansion

set MODE=%1
if "%MODE%"=="" set MODE=both

if "%MODE%"=="both" goto :stop_both
if "%MODE%"=="backend" goto :stop_backend_only
if "%MODE%"=="frontend" goto :stop_frontend_only
goto :usage

:stop_both
call :stop_backend
call :stop_frontend
echo [INFO] All services stopped
goto :end

:stop_backend_only
call :stop_backend
echo [INFO] Backend stopped
goto :end

:stop_frontend_only
call :stop_frontend
echo [INFO] Frontend stopped
goto :end

:stop_backend
echo [INFO] Stopping backend server...

REM Kill Java processes running Spring Boot
for /f "tokens=2" %%a in ('tasklist /fi "imagename eq java.exe" /fo list ^| findstr "PID"') do (
    wmic process where "ProcessId=%%a" get CommandLine 2>nul | findstr /i "spring-boot" >nul
    if !errorlevel! equ 0 (
        echo [INFO] Killing Java process %%a
        taskkill /f /pid %%a >nul 2>&1
    )
)

REM Also try to kill by port 8080
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo [INFO] Killing process on port 8080: %%a
    taskkill /f /pid %%a >nul 2>&1
)

exit /b 0

:stop_frontend
echo [INFO] Stopping frontend server...

REM Kill Node.js processes on port 3000
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":3000" ^| findstr "LISTENING"') do (
    echo [INFO] Killing process on port 3000: %%a
    taskkill /f /pid %%a >nul 2>&1
)

exit /b 0

:usage
echo Usage: %~nx0 [both^|frontend^|backend]
echo   both     - Stop both frontend and backend (default)
echo   frontend - Stop only the frontend
echo   backend  - Stop only the backend
exit /b 1

:end
endlocal

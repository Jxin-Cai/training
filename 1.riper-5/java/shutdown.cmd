@echo off
REM CMS系统停止脚本 - Windows
REM 支持：shutdown.cmd | shutdown.cmd frontend | shutdown.cmd backend

setlocal

set TARGET=%1

if "%TARGET%"=="frontend" (
    goto SHUTDOWN_FRONTEND
) else if "%TARGET%"=="backend" (
    goto SHUTDOWN_BACKEND
) else (
    goto SHUTDOWN_ALL
)

:SHUTDOWN_FRONTEND
echo ========================================
echo 停止前端服务...
echo ========================================

REM 查找占用3000端口的进程
for /f "tokens=5" %%a in ('netstat -aon ^| find ":3000" ^| find "LISTENING"') do (
    set PID=%%a
    goto KILL_FRONTEND
)

echo 前端服务未运行
goto END

:KILL_FRONTEND
echo 找到前端服务进程: %PID%
taskkill /F /PID %PID%
echo ✅ 前端服务已停止
goto END

:SHUTDOWN_BACKEND
echo ========================================
echo 停止后端服务...
echo ========================================

REM 查找占用8080端口的进程
for /f "tokens=5" %%a in ('netstat -aon ^| find ":8080" ^| find "LISTENING"') do (
    set PID=%%a
    goto KILL_BACKEND
)

echo 后端服务未运行
goto END

:KILL_BACKEND
echo 找到后端服务进程: %PID%
echo 正在优雅关闭...
taskkill /PID %PID%
timeout /t 5 /nobreak >nul

REM 检查是否还在运行
netstat -aon | find ":8080" | find "LISTENING" >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo 超时，强制终止...
    taskkill /F /PID %PID%
)
echo ✅ 后端服务已停止
goto END

:SHUTDOWN_ALL
echo ========================================
echo 停止CMS系统（前端+后端）
echo ========================================

call :SHUTDOWN_FRONTEND
echo.
call :SHUTDOWN_BACKEND

echo.
echo ========================================
echo 🎉 停止完成！
echo ========================================

:END
endlocal

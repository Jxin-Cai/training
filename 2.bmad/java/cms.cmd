@echo off
setlocal enabledelayedexpansion

REM Java CMS - 统一管理脚本 (Windows)
REM 用法: cms.cmd [start|stop|restart|status]

set PROJECT_ROOT=%~dp0
set FRONTEND_DIR=%PROJECT_ROOT%java-frontend
set BACKEND_DIR=%PROJECT_ROOT%java-backend
set BACKEND_PORT=8080
set FRONTEND_PORT=3000

REM 设置 Java 17 路径
if exist "C:\Program Files\Java\jdk-17" (
    set JAVA_HOME=C:\Program Files\Java\jdk-17
) else if exist "C:\Program Files\Eclipse Adoptium\jdk-17" (
    set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17
) else (
    REM 使用系统默认 Java
    set JAVA_HOME=
)

REM 颜色代码 (Windows 10+)
set GREEN=[92m
set RED=[91m
set YELLOW=[93m
set BLUE=[94m
set NC=[0m

:main
if "%1"=="" goto usage
if "%1"=="start" goto start_all
if "%1"=="stop" goto stop_all
if "%1"=="restart" goto restart_all
if "%1"=="status" goto show_status
if "%1"=="start-backend" goto start_backend
if "%1"=="start-frontend" goto start_frontend
if "%1"=="stop-backend" goto stop_backend
if "%1"=="stop-frontend" goto stop_frontend
goto usage

:start_all
echo.
echo ========================================
echo        启动 Java CMS 服务
echo ========================================
echo.

call :start_backend
if errorlevel 1 goto error

call :start_frontend
if errorlevel 1 goto error

echo.
echo ========================================
echo   所有服务启动完成！
echo ========================================
echo   前端: http://localhost:%FRONTEND_PORT%
echo   后端: http://localhost:%BACKEND_PORT%
echo ========================================
echo.
goto end

:stop_all
echo.
echo ========================================
echo        停止 Java CMS 服务
echo ========================================
echo.

call :stop_frontend
call :stop_backend

echo.
echo   所有服务已停止
echo.
goto end

:restart_all
call :stop_all
timeout /t 2 /nobreak > nul
call :start_all
goto end

:show_status
echo.
echo ========================================
echo         Java CMS 服务状态
echo ========================================
echo.

REM 检查后端端口
netstat -ano | findstr ":%BACKEND_PORT% " | findstr "LISTENING" > nul
if errorlevel 1 (
    echo   后端:  %RED%已停止%NC%
) else (
    echo   后端:  %GREEN%运行中%NC% (端口: %BACKEND_PORT%)
)

REM 检查前端端口
netstat -ano | findstr ":%FRONTEND_PORT% " | findstr "LISTENING" > nul
if errorlevel 1 (
    echo   前端:  %RED%已停止%NC%
) else (
    echo   前端:  %GREEN%运行中%NC% (端口: %FRONTEND_PORT%)
)

echo.
echo ========================================
echo   前端: http://localhost:%FRONTEND_PORT%
echo   后端: http://localhost:%BACKEND_PORT%
echo ========================================
echo.
goto end

:start_backend
echo [INFO] 启动后端服务...

REM 检查端口
netstat -ano | findstr ":%BACKEND_PORT% " | findstr "LISTENING" > nul
if not errorlevel 1 (
    echo [WARN] 端口 %BACKEND_PORT% 已被占用，后端可能已在运行
    goto :eof
)

cd /d "%BACKEND_DIR%"

REM 检查是否需要构建
if not exist "target\java-backend-0.0.1-SNAPSHOT.jar" (
    echo [INFO] 构建后端...
    call mvn clean package -DskipTests -q
    if errorlevel 1 (
        echo [ERROR] 后端构建失败
        exit /b 1
    )
)

REM 启动后端
if defined JAVA_HOME (
    start /b "" "%JAVA_HOME%\bin\java" -jar target\java-backend-0.0.1-SNAPSHOT.jar > "%PROJECT_ROOT%backend.log" 2>&1
) else (
    start /b "" java -jar target\java-backend-0.0.1-SNAPSHOT.jar > "%PROJECT_ROOT%backend.log" 2>&1
)

REM 等待启动
echo [INFO] 等待后端启动...
set /a count=0
:wait_backend
timeout /t 1 /nobreak > nul
netstat -ano | findstr ":%BACKEND_PORT% " | findstr "LISTENING" > nul
if errorlevel 1 (
    set /a count+=1
    if !count! lss 30 goto wait_backend
    echo [ERROR] 后端启动超时
    exit /b 1
)

echo [SUCCESS] 后端启动成功 (端口: %BACKEND_PORT%)
goto :eof

:start_frontend
echo [INFO] 启动前端服务...

REM 检查端口
netstat -ano | findstr ":%FRONTEND_PORT% " | findstr "LISTENING" > nul
if not errorlevel 1 (
    echo [WARN] 端口 %FRONTEND_PORT% 已被占用，前端可能已在运行
    goto :eof
)

cd /d "%FRONTEND_DIR%"

REM 检查依赖
if not exist "node_modules" (
    echo [INFO] 安装前端依赖...
    call npm install --silent
    if errorlevel 1 (
        echo [ERROR] 前端依赖安装失败
        exit /b 1
    )
)

REM 启动前端
start /b "" npm run dev > "%PROJECT_ROOT%frontend.log" 2>&1

REM 等待启动
echo [INFO] 等待前端启动...
set /a count=0
:wait_frontend
timeout /t 1 /nobreak > nul
netstat -ano | findstr ":%FRONTEND_PORT% " | findstr "LISTENING" > nul
if errorlevel 1 (
    set /a count+=1
    if !count! lss 30 goto wait_frontend
    echo [ERROR] 前端启动超时
    exit /b 1
)

echo [SUCCESS] 前端启动成功 (端口: %FRONTEND_PORT%)
goto :eof

:stop_backend
echo [INFO] 停止后端服务...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%BACKEND_PORT% " ^| findstr "LISTENING"') do (
    taskkill /pid %%a /f > nul 2>&1
)
echo [SUCCESS] 后端已停止
goto :eof

:stop_frontend
echo [INFO] 停止前端服务...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%FRONTEND_PORT% " ^| findstr "LISTENING"') do (
    taskkill /pid %%a /f > nul 2>&1
)
REM 也尝试关闭 node 进程
taskkill /im node.exe /f > nul 2>&1
echo [SUCCESS] 前端已停止
goto :eof

:usage
echo.
echo 用法: %~nx0 ^<命令^>
echo.
echo 命令:
echo   start           启动所有服务
echo   stop            停止所有服务
echo   restart         重启所有服务
echo   status          查看服务状态
echo.
echo   start-backend   仅启动后端
echo   start-frontend  仅启动前端
echo   stop-backend    仅停止后端
echo   stop-frontend   仅停止前端
echo.
goto end

:error
echo.
echo [ERROR] 启动失败，请检查日志
echo.

:end
endlocal

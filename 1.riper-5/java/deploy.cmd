@echo off
REM CMS系统部署脚本 - Windows
REM 支持：deploy.cmd | deploy.cmd frontend | deploy.cmd backend

setlocal

set TARGET=%1

if "%TARGET%"=="frontend" (
    goto DEPLOY_FRONTEND
) else if "%TARGET%"=="backend" (
    goto DEPLOY_BACKEND
) else (
    goto DEPLOY_ALL
)

:DEPLOY_FRONTEND
echo ========================================
echo 开始部署前端...
echo ========================================

cd frontend

echo 1. 安装依赖...
call npm install

echo 2. 构建生产版本...
call npm run build

echo 3. 启动前端服务（端口3000）...
REM 检查http-server是否安装
where http-server >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo http-server未安装，正在安装...
    call npm install -g http-server
)

REM 后台启动http-server
start /B http-server dist -p 3000 > frontend.log 2>&1

cd ..

echo ✅ 前端部署完成！
echo 访问地址: http://localhost:3000
goto END

:DEPLOY_BACKEND
echo ========================================
echo 开始部署后端...
echo ========================================

cd backend

echo 1. 清理并打包...
call mvn clean package -DskipTests

echo 2. 启动后端服务（端口8080）...
REM 后台启动Spring Boot应用
start /B java -jar target\cms-backend.jar > backend.log 2>&1

cd ..

echo ✅ 后端部署完成！
echo 访问地址: http://localhost:8080
echo H2控制台: http://localhost:8080/h2-console
goto END

:DEPLOY_ALL
echo ========================================
echo 开始部署CMS系统（前端+后端）
echo ========================================

call :DEPLOY_BACKEND
echo.
timeout /t 2 /nobreak >nul
call :DEPLOY_FRONTEND

echo.
echo ========================================
echo 🎉 部署完成！
echo ========================================
echo 前台访问: http://localhost:3000
echo 后台管理: http://localhost:3000/admin/categories
echo 后端API: http://localhost:8080/api
echo H2控制台: http://localhost:8080/h2-console

:END
endlocal

#!/bin/bash

# CMS系统部署脚本 - Linux/Mac
# 支持：./deploy.sh | ./deploy.sh frontend | ./deploy.sh backend

set -e

TARGET=$1

# 部署前端
deploy_frontend() {
    echo "========================================"
    echo "开始部署前端..."
    echo "========================================"
    
    cd frontend
    
    echo "1. 安装依赖..."
    npm install
    
    echo "2. 构建生产版本..."
    npm run build
    
    echo "3. 启动前端服务（端口3000）..."
    # 检查http-server是否安装
    if ! command -v http-server &> /dev/null; then
        echo "http-server未安装，正在安装..."
        npm install -g http-server
    fi
    
    # 后台启动http-server
    nohup http-server dist -p 3000 > frontend.log 2>&1 &
    echo "前端服务PID: $!"
    
    cd ..
    
    echo "✅ 前端部署完成！"
    echo "访问地址: http://localhost:3000"
}

# 部署后端
deploy_backend() {
    echo "========================================"
    echo "开始部署后端..."
    echo "========================================"
    
    cd backend
    
    echo "1. 清理并打包..."
    mvn clean package -DskipTests
    
    echo "2. 启动后端服务（端口8080）..."
    # 后台启动Spring Boot应用
    nohup java -jar target/cms-backend.jar > backend.log 2>&1 &
    echo "后端服务PID: $!"
    
    cd ..
    
    echo "✅ 后端部署完成！"
    echo "访问地址: http://localhost:8080"
    echo "H2控制台: http://localhost:8080/h2-console"
}

# 主逻辑
if [ "$TARGET" = "frontend" ]; then
    deploy_frontend
elif [ "$TARGET" = "backend" ]; then
    deploy_backend
else
    echo "========================================"
    echo "开始部署CMS系统（前端+后端）"
    echo "========================================"
    
    deploy_backend
    echo ""
    sleep 2
    deploy_frontend
    
    echo ""
    echo "========================================"
    echo "🎉 部署完成！"
    echo "========================================"
    echo "前台访问: http://localhost:3000"
    echo "后台管理: http://localhost:3000/admin/categories"
    echo "后端API: http://localhost:8080/api"
    echo "H2控制台: http://localhost:8080/h2-console"
fi

#!/bin/bash

# CMS系统停止脚本 - Linux/Mac
# 支持：./shutdown.sh | ./shutdown.sh frontend | ./shutdown.sh backend

TARGET=$1

# 停止前端
shutdown_frontend() {
    echo "========================================"
    echo "停止前端服务..."
    echo "========================================"
    
    # 查找http-server进程（端口3000）
    PID=$(lsof -ti:3000)
    
    if [ -z "$PID" ]; then
        echo "前端服务未运行"
    else
        echo "找到前端服务进程: $PID"
        kill $PID
        sleep 1
        
        # 检查是否成功停止
        if lsof -ti:3000 > /dev/null; then
            echo "进程未响应，强制终止..."
            kill -9 $PID
        fi
        
        echo "✅ 前端服务已停止"
    fi
}

# 停止后端
shutdown_backend() {
    echo "========================================"
    echo "停止后端服务..."
    echo "========================================"
    
    # 查找Spring Boot进程（端口8080）
    PID=$(lsof -ti:8080)
    
    if [ -z "$PID" ]; then
        echo "后端服务未运行"
    else
        echo "找到后端服务进程: $PID"
        echo "正在优雅关闭（最多等待30秒）..."
        kill $PID
        
        # 等待最多30秒
        for i in {1..30}; do
            if ! lsof -ti:8080 > /dev/null; then
                echo "✅ 后端服务已停止"
                return
            fi
            sleep 1
        done
        
        # 超时，强制终止
        if lsof -ti:8080 > /dev/null; then
            echo "超时，强制终止..."
            kill -9 $PID
            echo "✅ 后端服务已强制停止"
        fi
    fi
}

# 主逻辑
if [ "$TARGET" = "frontend" ]; then
    shutdown_frontend
elif [ "$TARGET" = "backend" ]; then
    shutdown_backend
else
    echo "========================================"
    echo "停止CMS系统（前端+后端）"
    echo "========================================"
    
    shutdown_frontend
    echo ""
    shutdown_backend
    
    echo ""
    echo "========================================"
    echo "🎉 停止完成！"
    echo "========================================"
fi

#!/bin/bash

# CMS 停止脚本

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "停止 CMS 服务..."

# 停止后端
if [ -f "$PROJECT_DIR/backend.pid" ]; then
    BACKEND_PID=$(cat "$PROJECT_DIR/backend.pid")
    if kill -0 $BACKEND_PID 2>/dev/null; then
        kill $BACKEND_PID
        echo "✓ 后端已停止 (PID: $BACKEND_PID)"
    fi
    rm "$PROJECT_DIR/backend.pid"
fi

# 停止前端
if [ -f "$PROJECT_DIR/frontend.pid" ]; then
    FRONTEND_PID=$(cat "$PROJECT_DIR/frontend.pid")
    if kill -0 $FRONTEND_PID 2>/dev/null; then
        kill $FRONTEND_PID
        echo "✓ 前端已停止 (PID: $FRONTEND_PID)"
    fi
    rm "$PROJECT_DIR/frontend.pid"
fi

# 确保端口释放
pkill -f "spring-boot:run" 2>/dev/null
pkill -f "vite" 2>/dev/null

echo "完成!"

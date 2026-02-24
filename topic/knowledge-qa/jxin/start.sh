#!/bin/bash

echo "=============================================="
echo "🚀 RAG 知识问答系统 - 启动中..."
echo "=============================================="

cd "$(dirname "$0")"

# 创建必要目录
mkdir -p data/documents data/chroma_db

# 检查并创建 Python 虚拟环境
if [ ! -d "backend/.venv" ]; then
    echo "📦 创建 Python 虚拟环境..."
    cd backend
    python3 -m venv .venv
    source .venv/bin/activate
    pip install -r requirements.txt
    cd ..
fi

# 启动前端（后台运行）
echo "🎨 启动前端服务..."
cd frontend
if [ ! -d "node_modules" ]; then
    echo "📦 安装前端依赖..."
    npm install
fi
npm run dev &
FRONTEND_PID=$!
cd ..

# 保存前端 PID
echo $FRONTEND_PID > .frontend.pid

echo ""
echo "=============================================="
echo "✅ 前端已启动: http://localhost:5173"
echo "=============================================="
echo ""
echo "🔧 启动后端服务 (日志输出到当前终端)..."
echo "   按 Ctrl+C 可停止所有服务"
echo ""
echo "   前端: http://localhost:5173"
echo "   后端: http://localhost:8000"
echo "   API文档: http://localhost:8000/docs"
echo ""
echo "=============================================="
echo ""

# 启动后端（前台运行，显示日志）
cd backend
source .venv/bin/activate

# 设置 trap 来清理前端进程
trap 'echo ""; echo "🛑 停止所有服务..."; kill $FRONTEND_PID 2>/dev/null; rm -f ../.frontend.pid; echo "✅ 已停止"; exit 0' SIGINT SIGTERM

# 运行后端（前台，日志直接显示）
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload

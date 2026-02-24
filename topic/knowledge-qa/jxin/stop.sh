#!/bin/bash

echo "🛑 停止 RAG Demo..."

cd "$(dirname "$0")"

if [ -f .backend.pid ]; then
    kill $(cat .backend.pid) 2>/dev/null
    rm .backend.pid
    echo "   后端已停止"
fi

if [ -f .frontend.pid ]; then
    kill $(cat .frontend.pid) 2>/dev/null
    rm .frontend.pid
    echo "   前端已停止"
fi

echo "✅ 已停止"

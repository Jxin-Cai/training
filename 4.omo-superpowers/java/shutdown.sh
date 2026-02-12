#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_DIR="$SCRIPT_DIR/.pids"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }

kill_by_port() {
    local port=$1
    local name=$2
    
    local pids=$(lsof -ti:$port 2>/dev/null)
    if [ -n "$pids" ]; then
        log_info "停止${name}进程 (端口$port)..."
        echo "$pids" | xargs kill -9 2>/dev/null || true
        sleep 1
        
        if lsof -i :$port > /dev/null 2>&1; then
            log_warn "${name}进程可能未完全停止"
            return 1
        fi
        log_info "${name}已停止"
        return 0
    else
        log_warn "${name}未运行 (端口$port空闲)"
        return 0
    fi
}

stop_backend() {
    kill_by_port 8080 "后端"
    rm -f "$PID_DIR/backend.pid" 2>/dev/null
}

stop_frontend() {
    kill_by_port 3000 "前端"
    rm -f "$PID_DIR/frontend.pid" 2>/dev/null
}

MODE="${1:-both}"

case "$MODE" in
    both)
        stop_frontend
        stop_backend
        echo ""
        log_info "所有服务已停止"
        echo ""
        ;;
    backend)
        stop_backend
        echo ""
        log_info "后端服务已停止"
        echo ""
        ;;
    frontend)
        stop_frontend
        echo ""
        log_info "前端服务已停止"
        echo ""
        ;;
    *)
        echo "用法: $0 [both|frontend|backend]"
        echo "  both     - 停止前端和后端 (默认)"
        echo "  frontend - 仅停止前端"
        echo "  backend  - 仅停止后端"
        exit 1
        ;;
esac

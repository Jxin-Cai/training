#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/backend"
FRONTEND_DIR="$SCRIPT_DIR/frontend"
PID_DIR="$SCRIPT_DIR/.pids"
LOG_DIR="$SCRIPT_DIR/.logs"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step() { echo -e "${CYAN}[STEP]${NC} $1"; }

mkdir -p "$PID_DIR" "$LOG_DIR"

check_port() {
    local port=$1
    if lsof -i :$port > /dev/null 2>&1; then
        return 0
    fi
    return 1
}

wait_for_port() {
    local port=$1
    local name=$2
    local max_wait=$3
    
    for i in $(seq 1 $max_wait); do
        if check_port $port; then
            return 0
        fi
        sleep 1
    done
    return 1
}

start_backend() {
    log_step "启动后端服务..."
    
    if check_port 8080; then
        log_warn "端口8080已被占用，尝试停止现有进程..."
        lsof -ti:8080 | xargs kill -9 2>/dev/null || true
        sleep 2
    fi
    
    cd "$BACKEND_DIR"
    
    if ! command -v mvn &> /dev/null; then
        log_error "Maven未安装，请先安装Maven"
        exit 1
    fi
    
    local JAR_FILE="$BACKEND_DIR/target/cms-backend-0.0.1-SNAPSHOT.jar"
    
    if [ ! -f "$JAR_FILE" ]; then
        log_info "构建后端项目..."
        mvn clean package -DskipTests -q
    fi
    
    if [ ! -f "$JAR_FILE" ]; then
        log_error "构建失败: JAR文件不存在"
        exit 1
    fi
    
    log_info "启动Spring Boot应用 (端口8080)..."
    nohup java -jar "$JAR_FILE" > "$LOG_DIR/backend.log" 2>&1 &
    local BACKEND_PID=$!
    echo $BACKEND_PID > "$PID_DIR/backend.pid"
    
    log_info "等待后端服务就绪..."
    if wait_for_port 8080 "backend" 60; then
        sleep 2
        if curl -s http://localhost:8080/api/categories > /dev/null 2>&1; then
            log_info "后端服务已启动 (PID: $BACKEND_PID)"
            return 0
        fi
    fi
    
    log_error "后端服务启动失败，查看日志: $LOG_DIR/backend.log"
    cat "$LOG_DIR/backend.log" | tail -20
    exit 1
}

start_frontend() {
    log_step "启动前端服务..."
    
    if check_port 3000; then
        log_warn "端口3000已被占用，尝试停止现有进程..."
        lsof -ti:3000 | xargs kill -9 2>/dev/null || true
        sleep 2
    fi
    
    cd "$FRONTEND_DIR"
    
    if ! command -v npm &> /dev/null; then
        log_error "npm未安装，请先安装Node.js"
        exit 1
    fi
    
    if [ ! -d "node_modules" ]; then
        log_info "安装前端依赖..."
        npm install --silent
    fi
    
    log_info "启动Vite开发服务器 (端口3000，代理/api到8080)..."
    nohup npm run dev > "$LOG_DIR/frontend.log" 2>&1 &
    local FRONTEND_PID=$!
    echo $FRONTEND_PID > "$PID_DIR/frontend.pid"
    
    log_info "等待前端服务就绪..."
    if wait_for_port 3000 "frontend" 30; then
        sleep 2
        log_info "前端服务已启动 (PID: $FRONTEND_PID)"
        return 0
    fi
    
    log_error "前端服务启动失败，查看日志: $LOG_DIR/frontend.log"
    exit 1
}

verify_proxy() {
    log_step "验证API代理..."
    
    local direct_result=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/categories 2>/dev/null)
    local proxy_result=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000/api/categories 2>/dev/null)
    
    if [ "$direct_result" = "200" ]; then
        log_info "后端API直接访问: OK (HTTP $direct_result)"
    else
        log_error "后端API直接访问失败: HTTP $direct_result"
        return 1
    fi
    
    if [ "$proxy_result" = "200" ]; then
        log_info "前端代理到后端: OK (HTTP $proxy_result)"
    else
        log_error "前端代理失败: HTTP $proxy_result"
        log_error "请确保前端是通过 npm run dev 启动的"
        return 1
    fi
    
    log_info "代理验证通过!"
    return 0
}

print_urls() {
    echo ""
    echo -e "${GREEN}=========================================${NC}"
    echo -e "${GREEN}  CMS内容管理系统启动成功!${NC}"
    echo -e "${GREEN}=========================================${NC}"
    echo ""
    echo -e "  ${CYAN}前台首页:${NC}   http://localhost:3000"
    echo -e "  ${CYAN}管理后台:${NC}   http://localhost:3000/admin"
    echo -e "  ${CYAN}后端API:${NC}    http://localhost:8080/api"
    echo ""
    echo -e "  ${YELLOW}日志文件:${NC}"
    echo -e "    后端: $LOG_DIR/backend.log"
    echo -e "    前端: $LOG_DIR/frontend.log"
    echo ""
    echo -e "${GREEN}=========================================${NC}"
    echo -e "  提示: 使用 ${YELLOW}./shutdown.sh${NC} 停止服务"
    echo -e "${GREEN}=========================================${NC}"
    echo ""
}

MODE="${1:-both}"

case "$MODE" in
    both)
        start_backend
        start_frontend
        sleep 2
        if verify_proxy; then
            print_urls
        else
            log_error "服务启动异常，请检查日志"
            exit 1
        fi
        ;;
    backend)
        start_backend
        echo ""
        log_info "后端服务已启动: http://localhost:8080/api"
        echo ""
        ;;
    frontend)
        if ! check_port 8080; then
            log_error "后端未运行! 前端代理需要后端在8080端口运行"
            log_error "请先运行: ./deploy.sh backend"
            exit 1
        fi
        start_frontend
        sleep 2
        verify_proxy
        echo ""
        log_info "前端服务已启动: http://localhost:3000"
        echo ""
        ;;
    *)
        echo "用法: $0 [both|frontend|backend]"
        echo "  both     - 启动前端和后端 (默认)"
        echo "  frontend - 仅启动前端 (需要后端已运行)"
        echo "  backend  - 仅启动后端"
        exit 1
        ;;
esac

#!/bin/bash

# Java CMS - 统一管理脚本
# 用法: ./cms.sh [start|stop|restart|status]

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
FRONTEND_DIR="$PROJECT_ROOT/java-frontend"
BACKEND_DIR="$PROJECT_ROOT/java-backend"
BACKEND_PID_FILE="$PROJECT_ROOT/.backend.pid"
FRONTEND_PID_FILE="$PROJECT_ROOT/.frontend.pid"
BACKEND_PORT=8080
FRONTEND_PORT=3000

# Java 17 路径
if [ -d "/Users/jxin/Library/Java/JavaVirtualMachines/graalvm-jdk-17.0.12/Contents/Home" ]; then
    export JAVA_HOME="/Users/jxin/Library/Java/JavaVirtualMachines/graalvm-jdk-17.0.12/Contents/Home"
elif [ -d "/Library/Java/JavaVirtualMachines/temurin-18.jdk/Contents/Home" ]; then
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-18.jdk/Contents/Home"
fi

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查端口是否被占用
check_port() {
    local port=$1
    if lsof -i :$port > /dev/null 2>&1; then
        return 0  # 端口被占用
    else
        return 1  # 端口空闲
    fi
}

# 等待端口可用
wait_for_port() {
    local port=$1
    local max_wait=30
    local count=0
    while check_port $port && [ $count -lt $max_wait ]; do
        sleep 1
        count=$((count + 1))
    done
    if [ $count -ge $max_wait ]; then
        return 1
    fi
    return 0
}

# 启动后端
start_backend() {
    log_info "启动后端服务..."
    
    if check_port $BACKEND_PORT; then
        log_warn "端口 $BACKEND_PORT 已被占用，后端可能已在运行"
        return 0
    fi
    
    cd "$BACKEND_DIR"
    
    # 检查是否需要构建
    if [ ! -f "target/java-backend-0.0.1-SNAPSHOT.jar" ]; then
        log_info "构建后端..."
        "$JAVA_HOME/bin/java" -version > /dev/null 2>&1 || { log_error "Java 未找到，请检查 JAVA_HOME"; return 1; }
        mvn clean package -DskipTests -q || { log_error "后端构建失败"; return 1; }
    fi
    
    # 启动后端
    nohup "$JAVA_HOME/bin/java" -jar target/java-backend-0.0.1-SNAPSHOT.jar > "$PROJECT_ROOT/backend.log" 2>&1 &
    echo $! > "$BACKEND_PID_FILE"
    
    # 等待启动
    log_info "等待后端启动..."
    local count=0
    while ! check_port $BACKEND_PORT && [ $count -lt 30 ]; do
        sleep 1
        count=$((count + 1))
    done
    
    if check_port $BACKEND_PORT; then
        log_success "后端启动成功 (PID: $(cat $BACKEND_PID_FILE), 端口: $BACKEND_PORT)"
    else
        log_error "后端启动失败，请查看 backend.log"
        return 1
    fi
}

# 启动前端
start_frontend() {
    log_info "启动前端服务..."
    
    if check_port $FRONTEND_PORT; then
        log_warn "端口 $FRONTEND_PORT 已被占用，前端可能已在运行"
        return 0
    fi
    
    cd "$FRONTEND_DIR"
    
    # 检查依赖
    if [ ! -d "node_modules" ]; then
        log_info "安装前端依赖..."
        npm install --silent || { log_error "前端依赖安装失败"; return 1; }
    fi
    
    # 启动前端
    nohup npm run dev > "$PROJECT_ROOT/frontend.log" 2>&1 &
    echo $! > "$FRONTEND_PID_FILE"
    
    # 等待启动
    log_info "等待前端启动..."
    local count=0
    while ! check_port $FRONTEND_PORT && [ $count -lt 30 ]; do
        sleep 1
        count=$((count + 1))
    done
    
    if check_port $FRONTEND_PORT; then
        log_success "前端启动成功 (PID: $(cat $FRONTEND_PID_FILE), 端口: $FRONTEND_PORT)"
    else
        log_error "前端启动失败，请查看 frontend.log"
        return 1
    fi
}

# 停止后端
stop_backend() {
    log_info "停止后端服务..."
    
    if [ -f "$BACKEND_PID_FILE" ]; then
        local pid=$(cat "$BACKEND_PID_FILE")
        if kill -0 $pid 2>/dev/null; then
            kill $pid 2>/dev/null || true
            sleep 2
            if kill -0 $pid 2>/dev/null; then
                kill -9 $pid 2>/dev/null || true
            fi
            log_success "后端已停止"
        else
            log_warn "后端进程不存在"
        fi
        rm -f "$BACKEND_PID_FILE"
    else
        # 尝试通过端口查找进程
        local pid=$(lsof -t -i :$BACKEND_PORT 2>/dev/null || true)
        if [ -n "$pid" ]; then
            kill $pid 2>/dev/null || true
            log_success "后端已停止 (通过端口查找)"
        else
            log_warn "后端未运行"
        fi
    fi
}

# 停止前端
stop_frontend() {
    log_info "停止前端服务..."
    
    if [ -f "$FRONTEND_PID_FILE" ]; then
        local pid=$(cat "$FRONTEND_PID_FILE")
        if kill -0 $pid 2>/dev/null; then
            # npm 可能启动子进程，需要杀掉整个进程组
            pkill -P $pid 2>/dev/null || true
            kill $pid 2>/dev/null || true
            sleep 2
            if kill -0 $pid 2>/dev/null; then
                kill -9 $pid 2>/dev/null || true
            fi
            log_success "前端已停止"
        else
            log_warn "前端进程不存在"
        fi
        rm -f "$FRONTEND_PID_FILE"
    else
        # 尝试通过端口查找进程
        local pid=$(lsof -t -i :$FRONTEND_PORT 2>/dev/null || true)
        if [ -n "$pid" ]; then
            kill $pid 2>/dev/null || true
            log_success "前端已停止 (通过端口查找)"
        else
            log_warn "前端未运行"
        fi
    fi
}

# 查看状态
show_status() {
    echo ""
    echo "========================================"
    echo "         Java CMS 服务状态"
    echo "========================================"
    
    # 后端状态
    echo ""
    if check_port $BACKEND_PORT; then
        local backend_pid=$(lsof -t -i :$BACKEND_PORT 2>/dev/null || echo "unknown")
        echo -e "后端:  ${GREEN}运行中${NC} (端口: $BACKEND_PORT, PID: $backend_pid)"
    else
        echo -e "后端:  ${RED}已停止${NC}"
    fi
    
    # 前端状态
    echo ""
    if check_port $FRONTEND_PORT; then
        local frontend_pid=$(lsof -t -i :$FRONTEND_PORT 2>/dev/null || echo "unknown")
        echo -e "前端:  ${GREEN}运行中${NC} (端口: $FRONTEND_PORT, PID: $frontend_pid)"
    else
        echo -e "前端:  ${RED}已停止${NC}"
    fi
    
    echo ""
    echo "========================================"
    echo "  前端: http://localhost:$FRONTEND_PORT"
    echo "  后端: http://localhost:$BACKEND_PORT"
    echo "========================================"
    echo ""
}

# 启动所有服务
start_all() {
    echo ""
    echo "========================================"
    echo "       启动 Java CMS 服务"
    echo "========================================"
    
    start_backend
    start_frontend
    
    echo ""
    echo "========================================"
    log_success "所有服务启动完成！"
    echo "========================================"
    echo "  前端: http://localhost:$FRONTEND_PORT"
    echo "  后端: http://localhost:$BACKEND_PORT"
    echo "  日志: $PROJECT_ROOT/backend.log"
    echo "        $PROJECT_ROOT/frontend.log"
    echo "========================================"
    echo ""
}

# 停止所有服务
stop_all() {
    echo ""
    echo "========================================"
    echo "       停止 Java CMS 服务"
    echo "========================================"
    
    stop_frontend
    stop_backend
    
    echo ""
    log_success "所有服务已停止"
    echo ""
}

# 重启所有服务
restart_all() {
    stop_all
    sleep 2
    start_all
}

# 主入口
case "$1" in
    start)
        start_all
        ;;
    stop)
        stop_all
        ;;
    restart)
        restart_all
        ;;
    status)
        show_status
        ;;
    start-backend)
        start_backend
        ;;
    start-frontend)
        start_frontend
        ;;
    stop-backend)
        stop_backend
        ;;
    stop-frontend)
        stop_frontend
        ;;
    logs)
        echo "后端日志 (Ctrl+C 退出):"
        tail -f "$PROJECT_ROOT/backend.log"
        ;;
    logs-frontend)
        echo "前端日志 (Ctrl+C 退出):"
        tail -f "$PROJECT_ROOT/frontend.log"
        ;;
    *)
        echo ""
        echo "用法: $0 <命令>"
        echo ""
        echo "命令:"
        echo "  start           启动所有服务"
        echo "  stop            停止所有服务"
        echo "  restart         重启所有服务"
        echo "  status          查看服务状态"
        echo ""
        echo "  start-backend   仅启动后端"
        echo "  start-frontend  仅启动前端"
        echo "  stop-backend    仅停止后端"
        echo "  stop-frontend   仅停止前端"
        echo ""
        echo "  logs            查看后端日志"
        echo "  logs-frontend   查看前端日志"
        echo ""
        ;;
esac

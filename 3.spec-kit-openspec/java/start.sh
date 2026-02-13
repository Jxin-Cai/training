#!/bin/bash

# CMS 一键启动脚本

echo "=========================================="
echo "CMS 内容管理系统 - 一键启动"
echo "=========================================="

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 检查是否已安装依赖
check_dependencies() {
    echo ""
    echo "1. 检查依赖..."

    # 检查 Java
    if ! command -v java &> /dev/null; then
        echo "❌ 未安装 Java 17+"
        exit 1
    fi
    echo "✓ Java: $(java -version 2>&1 | head -n 1)"

    # 检查 Maven
    if ! command -v mvn &> /dev/null; then
        echo "❌ 未安装 Maven"
        exit 1
    fi
    echo "✓ Maven: $(mvn -version 2>&1 | head -n 1)"

    # 检查 Node
    if ! command -v node &> /dev/null; then
        echo "❌ 未安装 Node.js"
        exit 1
    fi
    echo "✓ Node: $(node -v)"

    # 检查 npm
    if ! command -v npm &> /dev/null; then
        echo "❌ 未安装 npm"
        exit 1
    fi
    echo "✓ npm: $(npm -v)"
}

# 启动后端
start_backend() {
    echo ""
    echo "2. 启动后端 (Spring Boot)..."

    cd "$PROJECT_DIR/backend"

    # 检查是否需要编译
    if [ ! -f "target/cms-backend-1.0.0.jar" ]; then
        echo "   编译后端..."
        mvn clean package -DskipTests
    fi

    # 创建上传目录
    mkdir -p uploads

    # 后台启动后端
    echo "   启动中... (端口 8080)"
    mvn spring-boot:run > "$PROJECT_DIR/backend.log" 2>&1 &
    BACKEND_PID=$!
    echo $BACKEND_PID > "$PROJECT_DIR/backend.pid"
    echo "   后端 PID: $BACKEND_PID"

    # 等待后端启动
    echo "   等待后端启动..."
    for i in {1..30}; do
        if curl -s http://localhost:8080/api/categories > /dev/null 2>&1; then
            echo "   ✓ 后端启动成功!"
            return 0
        fi
        sleep 1
        echo -n "."
    done
    echo ""
    echo "   ⚠ 后端启动超时，请检查 backend.log"
}

# 安装前端依赖
install_frontend_deps() {
    echo ""
    echo "3. 检查前端依赖..."

    cd "$PROJECT_DIR/frontend"

    if [ ! -d "node_modules" ]; then
        echo "   安装前端依赖..."
        npm install
    else
        echo "   ✓ 前端依赖已安装"
    fi
}

# 启动前端
start_frontend() {
    echo ""
    echo "4. 启动前端 (Vite)..."

    cd "$PROJECT_DIR/frontend"

    # 后台启动前端
    echo "   启动中... (端口 5173)"
    npm run dev > "$PROJECT_DIR/frontend.log" 2>&1 &
    FRONTEND_PID=$!
    echo $FRONTEND_PID > "$PROJECT_DIR/frontend.pid"
    echo "   前端 PID: $FRONTEND_PID"

    sleep 3
    echo "   ✓ 前端启动成功!"
}

# 显示访问信息
show_info() {
    echo ""
    echo "=========================================="
    echo "启动完成!"
    echo "=========================================="
    echo ""
    echo "访问地址:"
    echo "  前台首页:    http://localhost:5173"
    echo "  管理后台:    http://localhost:5173/admin"
    echo "  H2 控制台:   http://localhost:8080/h2-console"
    echo "               (JDBC URL: jdbc:h2:mem:cmsdb, 用户: sa)"
    echo ""
    echo "日志文件:"
    echo "  后端日志: $PROJECT_DIR/backend.log"
    echo "  前端日志: $PROJECT_DIR/frontend.log"
    echo ""
    echo "停止服务: ./stop.sh"
    echo "=========================================="

    # 尝试自动打开浏览器
    if command -v open &> /dev/null; then
        sleep 2
        open http://localhost:5173
    elif command -v xdg-open &> /dev/null; then
        sleep 2
        xdg-open http://localhost:5173
    fi
}

# 主流程
main() {
    check_dependencies
    start_backend
    install_frontend_deps
    start_frontend
    show_info
}

main

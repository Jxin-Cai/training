#!/bin/bash

# Java CMS - Deployment Script (Linux/Mac)
# Usage: ./deploy.sh [frontend|backend]

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
FRONTEND_DIR="$PROJECT_ROOT/java-frontend"
BACKEND_DIR="$PROJECT_ROOT/java-backend"

echo "========================================"
echo "  Java CMS Deployment Script"
echo "========================================"

deploy_frontend() {
    echo ""
    echo "[Frontend] Starting deployment..."
    cd "$FRONTEND_DIR"
    
    # Check if node_modules exists
    if [ ! -d "node_modules" ]; then
        echo "[Frontend] Installing dependencies..."
        npm install
    fi
    
    echo "[Frontend] Building production version..."
    npm run build
    
    echo "[Frontend] Starting server on port 3000..."
    npm run serve &
    echo "[Frontend] Server started!"
}

deploy_backend() {
    echo ""
    echo "[Backend] Starting deployment..."
    cd "$BACKEND_DIR"
    
    # Set JAVA_HOME to Java 17 if available
    if [ -d "/Users/jxin/Library/Java/JavaVirtualMachines/graalvm-jdk-17.0.12/Contents/Home" ]; then
        export JAVA_HOME="/Users/jxin/Library/Java/JavaVirtualMachines/graalvm-jdk-17.0.12/Contents/Home"
        echo "[Backend] Using Java 17 from $JAVA_HOME"
    fi
    
    echo "[Backend] Building JAR package..."
    mvn clean package -DskipTests
    
    echo "[Backend] Starting Spring Boot on port 8080..."
    "$JAVA_HOME/bin/java" -jar target/java-backend-0.0.1-SNAPSHOT.jar &
    echo "[Backend] Server started!"
}

# Main logic
case "$1" in
    frontend)
        deploy_frontend
        ;;
    backend)
        deploy_backend
        ;;
    *)
        deploy_frontend
        deploy_backend
        ;;
esac

echo ""
echo "========================================"
echo "  Deployment Complete!"
echo "========================================"
echo "  Frontend: http://localhost:3000"
echo "  Backend:  http://localhost:8080"
echo "========================================"

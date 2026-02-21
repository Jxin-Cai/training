#!/bin/bash

# Java CMS - Shutdown Script (Linux/Mac)
# Usage: ./shutdown.sh [frontend|backend]

echo "========================================"
echo "  Java CMS Shutdown Script"
echo "========================================"

shutdown_frontend() {
    echo ""
    echo "[Frontend] Stopping server..."
    
    # Find and kill frontend process (vite/serve on port 3000)
    FRONTEND_PID=$(lsof -ti:3000 2>/dev/null || true)
    
    if [ -n "$FRONTEND_PID" ]; then
        echo "[Frontend] Found process $FRONTEND_PID, sending SIGTERM..."
        kill -15 $FRONTEND_PID 2>/dev/null || true
        
        # Wait for graceful shutdown (max 10 seconds)
        for i in {1..10}; do
            if ! kill -0 $FRONTEND_PID 2>/dev/null; then
                echo "[Frontend] Server stopped gracefully."
                return 0
            fi
            sleep 1
        done
        
        # Force kill if still running
        echo "[Frontend] Force killing process..."
        kill -9 $FRONTEND_PID 2>/dev/null || true
        echo "[Frontend] Server stopped."
    else
        echo "[Frontend] No process found on port 3000."
    fi
}

shutdown_backend() {
    echo ""
    echo "[Backend] Stopping server..."
    
    # Find and kill backend process (java -jar on port 8080)
    BACKEND_PID=$(lsof -ti:8080 2>/dev/null || true)
    
    if [ -n "$BACKEND_PID" ]; then
        echo "[Backend] Found process $BACKEND_PID, sending SIGTERM..."
        kill -15 $BACKEND_PID 2>/dev/null || true
        
        # Wait for graceful shutdown (max 30 seconds)
        for i in {1..30}; do
            if ! kill -0 $BACKEND_PID 2>/dev/null; then
                echo "[Backend] Server stopped gracefully."
                return 0
            fi
            sleep 1
        done
        
        # Force kill if still running
        echo "[Backend] Force killing process..."
        kill -9 $BACKEND_PID 2>/dev/null || true
        echo "[Backend] Server stopped."
    else
        echo "[Backend] No process found on port 8080."
    fi
}

# Main logic
case "$1" in
    frontend)
        shutdown_frontend
        ;;
    backend)
        shutdown_backend
        ;;
    *)
        shutdown_frontend
        shutdown_backend
        ;;
esac

echo ""
echo "========================================"
echo "  Shutdown Complete!"
echo "========================================"

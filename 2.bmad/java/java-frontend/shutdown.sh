#!/bin/bash

# Java CMS Frontend - Shutdown Script (Linux/Mac)

echo "========================================"
echo "  Java CMS Frontend Shutdown"
echo "========================================"

# Find and kill frontend process on port 3000
FRONTEND_PID=$(lsof -ti:3000 2>/dev/null || true)

if [ -n "$FRONTEND_PID" ]; then
    echo "[Info] Found process $FRONTEND_PID, sending SIGTERM..."
    kill -15 $FRONTEND_PID 2>/dev/null || true
    
    # Wait for graceful shutdown (max 10 seconds)
    for i in {1..10}; do
        if ! kill -0 $FRONTEND_PID 2>/dev/null; then
            echo "[Info] Server stopped gracefully."
            exit 0
        fi
        sleep 1
    done
    
    # Force kill if still running
    echo "[Info] Force killing process..."
    kill -9 $FRONTEND_PID 2>/dev/null || true
    echo "[Info] Server stopped."
else
    echo "[Info] No process found on port 3000."
fi

echo "========================================"
echo "  Shutdown Complete!"
echo "========================================"

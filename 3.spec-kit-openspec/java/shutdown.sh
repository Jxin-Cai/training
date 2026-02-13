#!/bin/bash

echo "=== CMS Shutdown Script ==="

SHUTDOWN_MODE=${1:-"all"}

# Stop backend
stop_backend() {
    if [ -f logs/backend.pid ]; then
        echo "Stopping backend..."
        kill $(cat logs/backend.pid) 2>/dev/null
        rm -f logs/backend.pid
        echo "Backend stopped"
    else
        echo "Backend PID file not found"
    fi
}

# Stop frontend
stop_frontend() {
    if [ -f logs/frontend.pid ]; then
        echo "Stopping frontend..."
        kill $(cat logs/frontend.pid) 2>/dev/null
        rm -f logs/frontend.pid
        echo "Frontend stopped"
    else
        echo "Frontend PID file not found"
    fi
}

# Stop based on mode
case $SHUTDOWN_MODE in
    "backend")
        stop_backend
        ;;
    "frontend")
        stop_frontend
        ;;
    "all"|"")
        stop_backend
        stop_frontend
        ;;
    *)
        echo "Usage: $0 [backend|frontend|all]"
        exit 1
        ;;
esac

echo "=== Shutdown Complete ==="

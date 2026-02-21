#!/bin/bash

# Java CMS Frontend - Deployment Script (Linux/Mac)
# Usage: ./deploy.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "========================================"
echo "  Java CMS Frontend Deployment"
echo "========================================"

cd "$SCRIPT_DIR"

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "[Info] Installing dependencies..."
    npm install
fi

echo "[Info] Building production version..."
npm run build

echo "[Info] Starting server on port 3000..."
npm run serve

echo ""
echo "========================================"
echo "  Frontend running at: http://localhost:3000"
echo "========================================"

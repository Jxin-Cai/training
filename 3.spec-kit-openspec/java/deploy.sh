#!/bin/bash

echo "=== CMS Deployment Script ==="

# Check if we're deploying frontend, backend, or both
DEPLOY_MODE=${1:-"all"}

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Backend deployment
deploy_backend() {
    echo -e "${YELLOW}Deploying backend...${NC}"
    cd backend

    # Build with Maven
    echo "Building backend with Maven..."
    mvn clean package -DskipTests

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Backend build successful${NC}"

        # Start backend
        echo "Starting backend server..."
        nohup java -jar target/cms-backend-1.0.0.jar > ../logs/backend.log 2>&1 &
        echo $! > ../logs/backend.pid
        echo -e "${GREEN}Backend started on port 8080${NC}"
    else
        echo -e "\033[0;31mBackend build failed${NC}"
        exit 1
    fi

    cd ..
}

# Frontend deployment
deploy_frontend() {
    echo -e "${YELLOW}Deploying frontend...${NC}"
    cd frontend

    # Install dependencies
    echo "Installing dependencies..."
    npm install

    # Build frontend
    echo "Building frontend..."
    npm run build

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Frontend build successful${NC}"

        # Start frontend (using serve or similar)
        echo "Starting frontend server..."
        npx serve -s dist -l 5173 > ../logs/frontend.log 2>&1 &
        echo $! > ../logs/frontend.pid
        echo -e "${GREEN}Frontend started on port 5173${NC}"
    else
        echo -e "\033[0;31mFrontend build failed${NC}"
        exit 1
    fi

    cd ..
}

# Create logs directory
mkdir -p logs

# Deploy based on mode
case $DEPLOY_MODE in
    "backend")
        deploy_backend
        ;;
    "frontend")
        deploy_frontend
        ;;
    "all"|"")
        deploy_backend
        deploy_frontend
        ;;
    *)
        echo "Usage: $0 [backend|frontend|all]"
        exit 1
        ;;
esac

echo -e "${GREEN}=== Deployment Complete ===${NC}"
echo "Backend API: http://localhost:8080"
echo "Frontend: http://localhost:5173"

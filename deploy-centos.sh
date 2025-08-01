#!/bin/bash
# EVCentral 3.7.1.2 Deployment Script for CentOS
# This script deploys the EVCentral application with Docker on CentOS

echo "========================================="
echo "EVCentral 3.7.1.2 Docker Deployment"
echo "========================================="

# Set variables
APP_DIR="/opt/evcentral3712"
REPO_URL="https://github.com/msukhum/evcentral3712.git"
LOGS_DIR="$APP_DIR/logs"

# Create application directory
echo "Creating application directory..."
sudo mkdir -p $APP_DIR
sudo chown $(whoami):$(whoami) $APP_DIR

# Navigate to application directory
cd $APP_DIR

# Stop existing containers if running
echo "Stopping existing containers..."
docker-compose down 2>/dev/null || true

# Pull latest code from GitHub
echo "Pulling latest code from GitHub..."
if [ -d ".git" ]; then
    git pull origin main
else
    git clone $REPO_URL .
fi

# Create logs directory with proper permissions
echo "Setting up logs directory..."
mkdir -p $LOGS_DIR
chmod 755 $LOGS_DIR

# Check if Docker is running
echo "Checking Docker service..."
if ! systemctl is-active --quiet docker; then
    echo "Starting Docker service..."
    sudo systemctl start docker
    sudo systemctl enable docker
fi

# Build and start the application
echo "Building and starting EVCentral application..."
docker-compose up --build -d

# Wait for application to start
echo "Waiting for application to start..."
sleep 10

# Check container status
echo "Checking container status..."
docker-compose ps

# Show logs
echo "Recent application logs:"
docker-compose logs --tail=20

echo "========================================="
echo "EVCentral Deployment Complete!"
echo "========================================="
echo "Application URL: http://$(hostname -I | awk '{print $1}'):8180/manager/dashboard"
echo "Login: admin / 1234567"
echo "Logs directory: $LOGS_DIR"
echo ""
echo "Commands:"
echo "  View logs:    docker-compose logs -f"
echo "  Stop app:     docker-compose down"
echo "  Restart app:  docker-compose restart"
echo "  Check status: docker-compose ps"
echo "========================================="

#!/bin/bash

# SecureBank Simulator - Quick Start Script
# This script helps you quickly start the entire application

echo "🏦 SecureBank Simulator - Quick Start"
echo "====================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

echo "✅ Docker found"

# Check if Docker Compose is available
if ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose is not available. Please install Docker Compose."
    exit 1
fi

echo "✅ Docker Compose found"
echo ""

# Build and start all services
echo "🚀 Building and starting all services..."
echo "This may take a few minutes on first run..."
echo ""

docker compose up --build -d

# Wait for services to be healthy
echo ""
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check if services are running
if docker compose ps | grep -q "Up"; then
    echo ""
    echo "✅ Services are running!"
    echo ""
    echo "📊 Application URLs:"
    echo "   Frontend:  http://localhost:3000"
    echo "   Backend:   http://localhost:8080/api"
    echo "   MySQL:     localhost:3306"
    echo ""
    echo "📝 To view logs:        docker compose logs -f"
    echo "🛑 To stop services:    docker compose down"
    echo "🔄 To restart services: docker compose restart"
    echo ""
else
    echo "❌ Some services failed to start. Check logs with: docker compose logs"
    exit 1
fi

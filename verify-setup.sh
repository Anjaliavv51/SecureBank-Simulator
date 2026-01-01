#!/bin/bash

# SecureBank Simulator - System Verification Script
# This script checks if all required tools are installed

echo "🔍 SecureBank Simulator - System Check"
echo "======================================="
echo ""

# Track if any checks fail
FAILED=0

# Check Java
echo -n "Checking Java... "
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
    echo "✅ Found ($JAVA_VERSION)"
else
    echo "❌ Not found"
    echo "   Please install JDK 11 or higher"
    FAILED=1
fi

# Check Maven
echo -n "Checking Maven... "
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    echo "✅ Found ($MVN_VERSION)"
else
    echo "❌ Not found"
    echo "   Please install Maven 3.8 or higher"
    FAILED=1
fi

# Check Node.js
echo -n "Checking Node.js... "
if command -v node &> /dev/null; then
    NODE_VERSION=$(node -v)
    echo "✅ Found ($NODE_VERSION)"
else
    echo "❌ Not found"
    echo "   Please install Node.js 18 or higher"
    FAILED=1
fi

# Check npm
echo -n "Checking npm... "
if command -v npm &> /dev/null; then
    NPM_VERSION=$(npm -v)
    echo "✅ Found ($NPM_VERSION)"
else
    echo "❌ Not found"
    echo "   Please install npm (comes with Node.js)"
    FAILED=1
fi

# Check Docker
echo -n "Checking Docker... "
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version | awk '{print $3}' | sed 's/,//')
    echo "✅ Found ($DOCKER_VERSION)"
else
    echo "❌ Not found"
    echo "   Please install Docker"
    FAILED=1
fi

# Check Docker Compose
echo -n "Checking Docker Compose... "
if docker compose version &> /dev/null; then
    COMPOSE_VERSION=$(docker compose version | awk '{print $4}')
    echo "✅ Found ($COMPOSE_VERSION)"
else
    echo "❌ Not found"
    echo "   Please install Docker Compose"
    FAILED=1
fi

# Check Git
echo -n "Checking Git... "
if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version | awk '{print $3}')
    echo "✅ Found ($GIT_VERSION)"
else
    echo "⚠️  Not found (optional but recommended)"
fi

echo ""
echo "======================================="

if [ $FAILED -eq 0 ]; then
    echo "✅ All required tools are installed!"
    echo ""
    echo "Next steps:"
    echo "  1. Run: ./start.sh (for Docker deployment)"
    echo "  2. Or run: mvn spring-boot:run (for local backend)"
    echo "  3. Access frontend at http://localhost:3000"
    echo "  4. Access backend at http://localhost:8080/api"
else
    echo "❌ Some required tools are missing."
    echo "   Please install them before proceeding."
    exit 1
fi

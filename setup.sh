#!/bin/bash

# Business Extractor Setup Script
# This script will install all dependencies and prepare the project for running

echo "======================================"
echo "Business Extractor - Setup Script"
echo "======================================"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or later."
    exit 1
fi

java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed 's/^1\.//' | cut -d'.' -f1)
if [ "$java_version" -lt "17" ]; then
    echo "❌ Java version is less than 17. Please install Java 17 or later."
    exit 1
fi
echo "✅ Java is installed (version $java_version)"

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 16 or later."
    exit 1
fi

node_version=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$node_version" -lt "16" ]; then
    echo "❌ Node.js version is less than 16. Please install Node.js 16 or later."
    exit 1
fi
echo "✅ Node.js is installed (version $node_version)"

# Create necessary directories
echo "Creating export directories..."
mkdir -p exports/csv
mkdir -p exports/excel
echo "✅ Export directories created"

# Setup backend
echo "Setting up backend..."
cd backend

# Check if .env file exists, if not create it from .env.example
if [ ! -f .env ]; then
    if [ -f .env.example ]; then
        cp .env.example .env
        echo "✅ Created .env file from .env.example"
        echo "⚠️ Please edit .env file to add your Google Places API key"
    else
        echo "⚠️ .env.example file not found. Creating basic .env file..."
        cat > .env << EOL
# Google Places API Configuration
GOOGLE_PLACES_API_KEY=your_api_key_here

# Export Directories
EXPORT_CSV_DIR=exports/csv
EXPORT_EXCEL_DIR=exports/excel

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
EOL
        echo "✅ Created basic .env file"
        echo "⚠️ Please edit .env file to add your Google Places API key"
    fi
fi

# Build backend with Maven
echo "Building backend with Maven..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Backend build failed."
    exit 1
fi
echo "✅ Backend built successfully"

# Set up frontend
echo "Setting up frontend..."
cd ../frontend

# Check if package.json exists
if [ ! -f package.json ]; then
    echo "❌ package.json not found. Make sure you're in the right directory."
    exit 1
fi

# Install frontend dependencies
echo "Installing frontend dependencies..."
npm install

if [ $? -ne 0 ]; then
    echo "❌ Frontend dependency installation failed."
    exit 1
fi
echo "✅ Frontend dependencies installed successfully"

# Return to project root
cd ..

# Create a simple start script if it doesn't exist
if [ ! -f start.sh ]; then
    echo "Creating start.sh script..."
    cat > start.sh << 'EOL'
#!/bin/bash

# This script starts both the backend and frontend of the MyBusiness Extractor app

# Print current directory
CURRENT_DIR=$(pwd)
echo "Working directory: $CURRENT_DIR"

# Load backend environment variables from .env files
if [ -f backend/.env ]; then
  echo "Loading backend environment variables..."
  set -a
  source backend/.env
  set +a
  echo "Google Places API Key loaded"
fi

# Create export directories if they don't exist
mkdir -p exports/csv
mkdir -p exports/excel
echo "Export directories created"

# Start the backend
echo "Starting Spring Boot backend..."
cd backend
./mvnw spring-boot:run &
BACKEND_PID=$!
cd ..

# Give the backend time to start
echo "Waiting for backend to start..."
sleep 10

# Check if the backend is running
if curl -s http://localhost:8080 > /dev/null; then
  echo "Backend started successfully!"
else
  echo "Backend may not have started properly. Check logs."
fi

# Start the frontend
echo "Starting React frontend..."
cd frontend
REACT_APP_API_URL=http://localhost:8080/api npm start &
FRONTEND_PID=$!
cd ..

# Function to handle script termination
function cleanup {
  echo "Shutting down servers..."
  kill $BACKEND_PID 2>/dev/null
  kill $FRONTEND_PID 2>/dev/null
  echo "Servers stopped"
  exit 0
}

# Register cleanup function for script termination
trap cleanup SIGINT SIGTERM

echo "MyBusiness Extractor is running!"
echo "- Backend: http://localhost:8080"
echo "- Frontend: http://localhost:3000"
echo "Press Ctrl+C to stop both servers"

# Keep the script running
wait
EOL
    chmod +x start.sh
    echo "✅ start.sh script created"
fi

echo ""
echo "======================================"
echo "Setup Completed Successfully!"
echo "======================================"
echo ""
echo "To run the application:"
echo "1. Make sure you have set your Google Places API key in backend/.env"
echo "2. Run the start script: ./start.sh"
echo ""
echo "The application will be available at:"
echo "- Frontend: http://localhost:3000"
echo "- Backend: http://localhost:8080"
echo ""
echo "Enjoy using Business Extractor!" 
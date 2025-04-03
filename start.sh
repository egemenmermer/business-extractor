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
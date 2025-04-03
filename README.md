# MyBusiness Extractor

A full-stack web application that extracts business data from Google Places API, mimicking a desktop business data extraction tool. Built with Spring Boot (backend) and React (frontend).

![MyBusiness Extractor Screenshot](screenshot.png)

## Features

- Search for businesses using categories and locations
- Extract detailed business information including:
  - Business name
  - Address
  - City, State, Postal Code
  - Phone numbers
  - Emails (if available)
  - Website
  - Geo-coordinates
  - Google Maps links
- Real-time task progress tracking
- Export results as CSV or Excel
- Responsive UI with dark theme

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.4.4
- WebFlux for reactive API calls
- Apache POI for Excel export

### Frontend
- React 19 with TypeScript
- Tailwind CSS for styling
- Axios for API communication
- Context API for state management

## Setup & Installation

### Prerequisites
- Java 17+
- Node.js 16+
- npm 8+
- Google Places API key

### Backend Setup
1. Navigate to the backend directory:
   ```
   cd backend
   ```

2. Add your Google Places API key to `src/main/resources/application.properties` or as an environment variable:
   ```
   GOOGLE_PLACES_API_KEY=your_api_key_here
   ```

3. Build and run the Spring Boot application:
   ```
   ./mvnw spring-boot:run
   ```
   The backend will start on http://localhost:8080

### Frontend Setup
1. Navigate to the frontend directory:
   ```
   cd frontend
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Start the development server:
   ```
   npm start
   ```
   The frontend will start on http://localhost:3000

## Usage

1. Add categories (e.g., "Restaurant", "Hotel", "Lawyer") to the Categories panel
2. Add locations (e.g., "New York", "Los Angeles", "Chicago") to the Locations panel
3. Select the categories and locations you want to search for
4. Click "Get Data" to start the search
5. Monitor progress in the Tasks panel
6. View and filter results in the Results table
7. Export results as CSV or Excel

## API Endpoints

- `POST /api/search` - Start a new search with categories and locations
- `GET /api/tasks` - Get the status of all tasks
- `GET /api/results` - Get the current search results
- `POST /api/export` - Export results as CSV or Excel

## Project Structure

- **backend/**
  - src/main/java/com/mybusinessextractor/
    - controller/ - REST API controllers
    - service/ - Business logic services
    - dto/ - Data transfer objects
    - model/ - Business data models
    - util/ - Utility classes
    - config/ - Configuration classes

- **frontend/**
  - src/
    - components/ - React components
    - context/ - React context for state management
    - services/ - API communication services
    - types/ - TypeScript interfaces

## License

MIT

# MyBusiness Extractor

A full-stack web application that extracts business data from Google Places API,  Built with Spring Boot (backend) and React (frontend).

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
- Spring Boot
- Spring Security with JWT
- PostgreSQL
- Hibernate/JPA
- Flyway for database migrations
- WebClient for API calls

### Frontend
- React
- TypeScript
- React Router
- Axios
- TailwindCSS

## Environment Configuration

The application uses environment variables for configuration to keep sensitive information secure. 

1. Create a `.env` file in the backend directory using the provided `.env.example` as a template.
2. Update the values in the `.env` file with your specific configuration.
3. Make sure not to commit the `.env` file to version control (it's already included in `.gitignore`).

Key environment variables that need to be configured:

- `POSTGRES_PASSWORD`: Your database password
- `GOOGLE_PLACES_API_KEY`: Your Google Places API key
- `JWT_SECRET`: A secure secret for JWT token generation
- `MAIL_USERNAME`, `MAIL_PASSWORD`: Your email credentials for sending verification emails

## Installation & Setup

### Prerequisites
- Java 17
- Node.js 14+ and npm
- PostgreSQL

### Backend Setup
1. Navigate to the `backend` directory
2. Create a `.env` file from the `.env.example` template
3. Run `./mvnw clean install` to build the project
4. Run `./mvnw spring-boot:run` to start the backend server

### Frontend Setup
1. Navigate to the `frontend` directory
2. Run `npm install` to install dependencies
3. Run `npm start` to start the frontend development server

### Database Setup
1. Install PostgreSQL and create a database named `business_extractor`
2. The application uses Flyway migrations to set up the database schema automatically

## Running the Application

Use the provided `start.sh` script to run both the backend and frontend:

```bash
chmod +x start.sh
./start.sh
```

The application will be available at:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080

## License

[MIT License](LICENSE)

## Contributors

- [Your Name](https://github.com/yourusername)

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

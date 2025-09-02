# Patient Dashboard & LLM Agent Analytics Platform

A full-stack microservices platform for healthcare analytics, claims management, and LLM-powered Q&A.  
Built with Spring Boot (Java 17+), Next.js (TypeScript, Tailwind CSS), PostgreSQL, and OpenAI integration.

## Features

### Backend (Spring Boot)
- RESTful API for patients, claims, and analytics
- PostgreSQL integration via Spring Data JPA/Hibernate
- JWT-based authentication (Spring Security)
- Analytics endpoints:
  - Most expensive claim (patient, amount, hospital)
  - Highest claimer (patient with highest total claims)
  - Hospital with most unique patients
  - Top N hospitals by patients, claims, and cost
  - Patient search by name, FHIR ID, or ID
- LLM Agent service:
  - Natural language Q&A over analytics and patient/claim data
  - Custom rules for medical/claims queries
  - OpenAI GPT integration for fallback answers

### Frontend (Next.js 14)
- Modern dashboard UI with Tailwind CSS
- Patient and claim list/detail pages
- Analytics visualizations
- LLM Agent Q&A chat interface (multi-line input)
- Error/loading states and responsive design

### ETL & Test Data
- Python scripts for FHIR → PostgreSQL ETL
- Automated test data population for claims, hospitals, and descriptions
- **Test data is used for patient and claim information.**

### Security & DevOps
- Sensitive config values redacted and `.gitignore` enforced
- Example config files for easy setup
- CORS configuration for API access
- Secret scanning (gitleaks) and safe public repo setup

## Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL
- Python 3.8+ (for ETL scripts)

### Setup

1. **Clone the repo:**
   ```sh
   git clone git@github.com:udayganeshK/patient-dashboard.git
   cd patient-dashboard
   ```

2. **Configure environment variables:**
   - Copy and edit config files:
     - `backend/src/main/resources/application.properties.example` → `application.properties`
     - `frontend/.env.example` → `.env.local`
   - Fill in your database, JWT, and OpenAI API keys.

3. **Database setup:**
   - Create PostgreSQL database and user as per your config.
   - Run ETL scripts to populate test data (see `/llm-agent/util/`).

4. **Start backend:**
   ```sh
   cd backend
   mvn spring-boot:run
   ```

5. **Start frontend:**
   ```sh
   cd frontend
   npm install
   npm run dev
   ```

## API Endpoints

- `/api/patients` - List/search patients
- `/api/claims` - List/search claims
- `/api/claims/summary` - Claims analytics summary
- `/api/llm-agent/ask` - LLM Q&A endpoint

## LLM Agent Rules

- Most expensive claim (patient, amount, hospital)
- Highest claimer (total claim cost)
- Hospital with most patients
- Top N hospitals by metrics
- Patient with highest claim and their most visited hospital (custom rule)
- Fallback: global database summary

## Testing

- Unit tests for backend business logic
- Integration tests for API endpoints
- TestContainers for database testing
- Manual and automated secret scanning

## Security

- JWT authentication
- Input validation and error handling
- CORS and HTTPS-ready
- Secrets/configs excluded from repo

## Deployment

- AWS deployment scripts and guides included
- Docker Compose for local/prod environments

## License

MIT

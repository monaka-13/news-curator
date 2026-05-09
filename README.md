# news-curator

Portfolio project to collect news, search, and summarize by AI for people interested in specific topic like working abroad and nomad work.

## Implementation
### Prerequisites
- Windows 10/11 (or equivalent environment)
- Java 21+
- Docker Desktop (with `docker compose` available)

### Quick Start
1. Clone this repository.
2. Move to the Spring Boot app directory:
   - `cd demo`
3. Create your local environment file:
   - `copy .env.example .env`
4. Edit `.env` and set your local PostgreSQL password.
5. Run the app:
   - `.\mvnw.cmd spring-boot:run`

Spring Boot Docker Compose integration starts PostgreSQL from `demo/compose.yaml` automatically at startup.

### Verify Startup
Open a new terminal and run:

- `curl -UseBasicParsing http://localhost:8080/api/health`

Expected response:
- `{"status":"ok"}`

You can also confirm DB connectivity from startup logs with messages like:
- `Container demo-postgres-1 Healthy`
- `HikariPool-1 - Added connection`

### Local Services
- App: `http://localhost:8080`
- PostgreSQL: `localhost:5432`
  - DB/User/Password are loaded from `demo/.env`
  - Example values are in `demo/.env.example`

### Troubleshooting
- If `docker` is not found, ensure Docker Desktop is installed and running.
- If PowerShell warns on `curl`, use `-UseBasicParsing` or `Invoke-RestMethod`.
- If port `8080` or `5432` is already in use, stop the conflicting process/container and retry.

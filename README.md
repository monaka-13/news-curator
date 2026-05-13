# news-curator

Portfolio project to collect news, search, and summarize by AI for people interested in specific topic like working abroad and nomad work.

## Implementation

### Repository layout

| Directory | Description |
|-----------|-------------|
| `demo/` | Spring Boot API and PostgreSQL via Docker Compose |
| `web/` | Vite + React frontend |

### Prerequisites

- Windows 10/11 (or equivalent environment)
- Java 21+
- Docker Desktop (with `docker compose` available), running before you start the API
- Node.js (current LTS, e.g. 20.x or newer) and **npm**, if you want to run the `web` frontend

### Quick start (backend API)

1. Clone this repository.
2. Go to the Spring Boot app:
   - `cd demo`
3. Create your local environment file:
   - Windows: `copy .env.example .env`
   - macOS/Linux: `cp .env.example .env`
4. Edit `.env` and set your local PostgreSQL password (and other values if you changed them).
5. Start the app:
   - Windows: `.\mvnw.cmd spring-boot:run`
   - macOS/Linux: `./mvnw spring-boot:run`

Spring Boot Docker Compose integration starts PostgreSQL from `demo/compose.yaml` automatically at startup.

### Quick start (frontend)

The UI talks to the API over HTTP. Start the backend first, then:

1. From the repository root, go to the frontend:
   - `cd web`
2. Install dependencies (first time, or after `package.json` changes):
   - `npm install`
3. Start the dev server:
   - `npm run dev`
4. Open the URL printed in the terminal (usually `http://localhost:5173`).

Optional: create `web/.env` if the API is not at `http://localhost:8080`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

The backend enables CORS for the default Vite dev origins (`localhost:5173` and `127.0.0.1:5173`); adjust `demo/src/main/java/com/example/demo/WebConfig.java` if you use another port or host.

### Verify startup

**API** — in a separate terminal:

- Windows PowerShell: `curl -UseBasicParsing http://localhost:8080/api/health`
- Expected: `{"status":"ok"}`

You can also confirm DB connectivity from startup logs with messages like:

- `Container demo-postgres-1 Healthy`
- `HikariPool-1 - Added connection`

**Frontend** — with `npm run dev` running, load the app in the browser and try search or register; use the browser developer tools **Network** tab if requests fail.

### Local services

| Service | URL / host |
|---------|------------|
| API | `http://localhost:8080` |
| PostgreSQL | `localhost:5432` (credentials from `demo/.env`; see `demo/.env.example`) |
| Vite dev server (when running `web`) | typically `http://localhost:5173` |

### Production build (frontend)

From `web/`:

- `npm run build` — output in `web/dist/`

Serve `dist/` with any static host; configure that host and CORS (or a reverse proxy to the API) for your deployment.

### Troubleshooting

- If `docker` is not found, ensure Docker Desktop is installed and **running** before `spring-boot:run`.
- If PowerShell warns on `curl`, use `-UseBasicParsing` or `Invoke-RestMethod`.
- If port `8080` or `5432` is already in use, stop the conflicting process/container and retry.
- If `npm` or `node` is not found, install Node.js (LTS) and reopen the terminal.
- If the browser shows CORS errors, confirm the frontend origin matches `WebConfig`, or put the API and UI behind the same origin via a proxy.

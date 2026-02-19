# Deployment Guide

## Docker Compose (Recommended)

```bash
cp .env.example .env
docker compose up --build -d
docker compose ps
docker compose logs -f app
```

Stop:

```bash
docker compose down
```

Reset DB:

```bash
docker compose down -v
docker compose up --build -d
```

## Render

1. Push repository to GitHub.
2. Create managed PostgreSQL in Render.
3. Create Web Service from this repo using Dockerfile.
4. Set env vars:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
   - `JWT_SECRET`, `JWT_EXPIRATION_MS`
5. Deploy and validate:
   - `/swagger-ui/index.html`
   - `/v3/api-docs`

## Railway

1. Import repo to Railway.
2. Add PostgreSQL plugin.
3. Configure API service with Docker build.
4. Set production env vars as above.
5. Deploy and test auth + jobs endpoints.

## VPS (Ubuntu + Docker)

Install Docker:

```bash
sudo apt update
sudo apt install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo $VERSION_CODENAME) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

Deploy:

```bash
git clone <repo-url>
cd job-application-tracker-api
cp .env.example .env
docker compose up --build -d
docker compose logs -f app
```


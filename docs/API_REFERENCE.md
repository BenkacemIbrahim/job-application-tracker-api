# API Reference

Base URL:
- Local: `http://localhost:8080`

Swagger:
- `GET /swagger-ui/index.html`
- `GET /v3/api-docs`

## Authentication

### Register

- `POST /api/auth/register`

Request:

```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "Pass12345!"
}
```

Response `201`:

```json
{
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "role": "USER"
}
```

### Login

- `POST /api/auth/login`

Request:

```json
{
  "username": "john",
  "password": "Pass12345!"
}
```

Response `200`:

```json
{
  "token": "<jwt>",
  "tokenType": "Bearer",
  "expiresIn": 3600000
}
```

## Job Applications

All endpoints require:
- `Authorization: Bearer <jwt>`

### List Jobs

- `GET /api/jobs?page=0&size=10&sort=desc&status=APPLIED`

Query params:
- `page` default `0`
- `size` default `10`, max `100`
- `sort` values: `asc` or `desc` by `appliedDate`
- `status` optional enum: `APPLIED`, `INTERVIEW`, `REJECTED`, `OFFER`

### Create Job

- `POST /api/jobs`

Request:

```json
{
  "companyName": "OpenAI",
  "position": "Backend Engineer",
  "status": "APPLIED",
  "appliedDate": "2026-02-18",
  "notes": "Referral process"
}
```

Response `201`:

```json
{
  "id": 1,
  "companyName": "OpenAI",
  "position": "Backend Engineer",
  "status": "APPLIED",
  "appliedDate": "2026-02-18",
  "notes": "Referral process",
  "userId": 1,
  "createdAt": "2026-02-19T18:26:44.982318Z",
  "updatedAt": "2026-02-19T18:26:44.982332Z"
}
```

### Update Job

- `PUT /api/jobs/{id}`

### Delete Job

- `DELETE /api/jobs/{id}`

### Stats

- `GET /api/jobs/stats`

Response `200`:

```json
{
  "totalApplications": 5,
  "interviews": 2,
  "offers": 1,
  "rejected": 1
}
```

## Error Format

Response example:

```json
{
  "timestamp": "2026-02-19T18:29:30.851173802Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username/email or password",
  "path": "/api/auth/login"
}
```


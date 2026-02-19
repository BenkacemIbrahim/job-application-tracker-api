# Contributing Guide

Thanks for contributing to this project.

## Development Setup

1. Install Java 17 and Maven 3.9+.
2. Copy env file:
   - `cp .env.example .env` (Linux/macOS)
   - `Copy-Item .env.example .env` (PowerShell)
3. Start local infrastructure:
   - `docker compose up -d postgres`
4. Run tests:
   - `mvn clean test`

## Branch and Commit Conventions

- Branch naming:
  - `feat/<short-description>`
  - `fix/<short-description>`
  - `docs/<short-description>`
- Commit style:
  - `feat: ...`
  - `fix: ...`
  - `docs: ...`
  - `test: ...`
  - `chore: ...`

## Pull Request Checklist

- Keep PR scope focused.
- Add or update tests for behavior changes.
- Ensure `mvn clean test` passes locally.
- Update documentation when API or setup changes.
- Fill the PR template completely.

## Code Quality Expectations

- Follow layered architecture (`controller`, `service`, `repository`).
- Keep DTOs separate from entities.
- Validate inputs at API boundary.
- Keep business rules in service layer.
- Use meaningful logs and avoid sensitive data in logs.


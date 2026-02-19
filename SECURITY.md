# Security Policy

## Reporting a Vulnerability

If you discover a security issue, please do not open a public issue.

Report it privately with:
- Clear description of the vulnerability
- Reproduction steps
- Potential impact
- Suggested remediation (if available)

Until a dedicated security contact is configured, report through a private channel and allow reasonable time for remediation before public disclosure.

## Supported Versions

Security fixes are provided for the latest code on the default branch.

## Security Best Practices for Deployments

- Use strong `JWT_SECRET` values in production.
- Rotate credentials regularly.
- Do not expose PostgreSQL publicly without network controls.
- Run with `SPRING_PROFILES_ACTIVE=prod`.
- Keep dependencies up to date.


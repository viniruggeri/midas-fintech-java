# Copilot Instructions - Midas Fintech Java (CP1)

## Goal

Keep implementation simple, testable, and easy to explain in oral evaluation while meeting FIAP requirements:

- Frontend web layer
- Flyway versioned migrations
- Spring Security with at least 2 user roles and route protection
- At least 2 complete functional flows (not only CRUD)

## Technical Stack

- Java 21
- Spring Boot 3.2.x
- Maven
- Spring MVC + Thymeleaf for frontend
- Spring Security (session + form login)
- Flyway for DB migrations
- JPA/Hibernate
- H2 (dev) and Oracle (prod)

## Architecture Rules

- Keep `controller` thin. Only request/response orchestration and basic validation.
- Put business rules in `service`.
- Keep persistence logic in `repository` only.
- Use DTOs for input/output, do not expose entities directly in views/forms.
- Prefer small methods with clear names.

## Simplicity Rules (Critical)

- Prefer straightforward code over advanced patterns.
- Do not introduce new frameworks unless required.
- Avoid abstractions that are hard to explain in oral defense.
- One responsibility per class/method (SOLID).
- Reuse validation helpers to avoid duplication (DRY).

## Security Rules

- Implement at least two profiles: `ADMIN` and `CLIENT`.
- Protect routes using role-based authorization:
  - Client area only for `CLIENT`
  - Admin area only for `ADMIN`
- Keep auth flow simple with Spring Security form login.

## Flyway Rules

- Every schema/data change must be a migration under `src/main/resources/db/migration`.
- Keep migrations small and sequential (`V1__`, `V2__`, ...).
- Include seed users/roles in an early migration when needed for demo.

## Frontend Rules

- Use Thymeleaf templates under `src/main/resources/templates`.
- Start with minimal pages only:
  - login
  - client dashboard
  - transfer form
  - admin panel
- Add basic field validation and user-friendly error messages.

## Functional Flows (Prefer These)

- Flow 1 (Client): transfer between accounts with business validations.
- Flow 2 (Admin): reverse/approve operation with access restriction and audit-safe behavior.

## Testing Rules

- Add focused tests for business-critical rules in service layer.
- Add at least one security route test (client cannot access admin).
- Keep tests readable and directly tied to requirements.

## Code Style

- Keep Portuguese naming where project already uses Portuguese domain names.
- Keep comments brief and only when needed to explain non-obvious logic.
- Do not refactor unrelated code when implementing a requirement.

## Delivery Mindset

When asked to implement features, always:

1. Map change to one FIAP requirement.
2. Implement minimal viable version.
3. Add/update tests for the changed behavior.
4. Keep explanation-ready code (easy to present orally).

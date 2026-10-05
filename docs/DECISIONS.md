# Architecture decisions

Short record of the choices made so far, and why.

## 1. Hexagonal architecture (ports and adapters)

The domain and application code contain the business rules and use plain Java. They never import Spring, JPA or Kafka classes. Technical details sit in adapters that implement ports (interfaces).

```
com.example.backend
├── domain/        entities and rules (no framework code)
├── application/   use cases
│   └── port/
│       ├── in/    what the outside can ask (e.g. CreateInvoiceUseCase)
│       └── out/   what the core needs (e.g. InvoiceRepository, EventPublisher)
└── adapter/
    ├── in/        REST controllers, Kafka consumers, CSV import, webhooks
    └── out/       JPA persistence, Kafka producer, Stripe, PDF, receipt extractor
```

**Why:** business rules are testable without a database, adapters can be swapped (e.g. in-memory events first, Kafka later), and it matches the architecture named in the job listings.

**Rule:** dependencies point inward only. The `domain` package has no framework imports at all. The `application` services may use Spring's `@Service` and `@Transactional` and nothing else from the framework (a deliberate trade: transaction boundaries belong to use cases).

**Ports:** inbound ports are grouped per area (`AuthUseCase`, `InvoiceUseCase`, ...) with their command records nested inside, rather than one interface per use case.

## 2. Money as integer minor units

Amounts are stored as integer cents plus a currency code, never floating point.

**Why:** floating point causes rounding errors that break ledger balances.

## 3. Append-only double-entry ledger

Every invoice, payment and credit note creates balanced entries. Entries are never edited or deleted. Corrections are new entries (e.g. credit notes, reversals).

**Why:** auditability, and a built-in correctness check: all entries for a transaction must sum to zero.

## 4. Multi-tenancy from the start

Every business record belongs to a workspace, and every query is scoped to it.

**Why:** adding more users or a paid tier later is an addition, not a rewrite.

## 5. The app tracks money, it never holds it

Payments are recorded or run through Stripe test mode. No custody of funds.

**Why:** holding other people's money is a regulated activity. Tracking and reconciliation are not.

## 6. Idempotency for external and mutating requests

Mutating API calls accept an `Idempotency-Key`, and webhooks and imports are safe to replay.

**Why:** retries and re-uploads must never create duplicate payments or ledger entries.

## 7. Database schema owned by Flyway

Hibernate only validates the schema (`ddl-auto: validate`). All changes are migrations in `backend/src/main/resources/db/migration`.

## 8. Secrets stay out of git

Credentials live in a gitignored `.env`. `.env.example` documents the variable names. Postgres is bound to `127.0.0.1` in Docker Compose.

## 9. Stack

Java 25 (LTS), Spring Boot 4.1, Maven, PostgreSQL 17 (Docker Compose), Flyway, Angular 22, Testcontainers for integration tests, GitHub Actions for CI.

## 10. Authentication: stateless JWT

Spring Security acts as an OAuth2 resource server and validates HS256 JWTs signed with `JWT_SECRET` (at least 32 bytes). The token carries the user id (`sub`) and the workspace id (`wid`). Every query is scoped by the workspace id taken from the token, never from request input.

**Why:** no server-side session state, and no extra dependency beyond Spring Security.

**Known trade-off:** the Angular app keeps the token in `localStorage`, which is exposed to XSS. Acceptable for now. A later hardening step is short-lived access tokens with a refresh token in an `HttpOnly` cookie.

## 11. Branching

`dev` is the working branch. `main` is production and is protected: changes arrive through pull requests.

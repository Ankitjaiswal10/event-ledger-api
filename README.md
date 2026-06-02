# Event Ledger API

A Spring Boot REST API that processes financial transaction events while supporting:

- Idempotent event processing
- Out-of-order event handling
- Balance computation
- Validation and error handling
- Automated testing
- H2 in-memory database

---

## Tech Stack

- Java 21
- Spring Boot 3.x
- Spring Data JPA
- H2 Database
- Maven
- JUnit 5
- MockMvc
- Swagger / OpenAPI

---

## Project Structure

src
├── main
│ ├── controller
│ ├── dto
│ ├── entity
│ ├── exception
│ ├── repository
│ ├── service
│ └── EventLedgerApplication
│
└── test
├── EventControllerTest
├── EventServiceTest
└── IntegrationTest

---

## API Documentation

The REST endpoints are documented in this README.
Swagger/OpenAPI support can be enabled via springdoc-openapi.

## Concurrency Handling

The current implementation performs an existence check
before insert to satisfy idempotency.

In a production environment, concurrency-safe idempotency
would be enforced using the database primary key
constraint on eventId together with transaction handling
and DataIntegrityViolationException processing.

## API Endpoints

### Create Event

POST /events

Example Request

```json
{
  "eventId": "evt-001",
  "accountId": "acct-123",
  "type": "CREDIT",
  "amount": 150.00,
  "currency": "USD",
  "eventTimestamp": "2026-05-15T14:02:11Z",
  "metadata": {
    "source": "mainframe-batch",
    "batchId": "B-9042"
  }
}

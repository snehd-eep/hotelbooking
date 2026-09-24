# Hotel Booking System

A scalable, extensible backend service for a hotel booking platform, built as part of the Rupeek SDE-3 Machine Coding Round.

## Prerequisites

- **Java 17+**
- **Maven 3.8+**

## How to Run

1. **Clone/Unzip the project** and navigate into the root directory (`hotelbooking`).
2. **Build the project** and run the test suite to ensure everything is working:
   ```bash
   mvn clean install
   ```
3. **Start the application**:
   ```bash
   mvn spring-boot:run
   ```
4. The server will start on `http://localhost:8080`.
5. **Testing the APIs:** A Postman collection (`Hotel_Booking_Postman_Collection.json`) is included in the root directory. Import it into Postman to easily exercise the core REST endpoints (Add Property, Add Room, Search Properties, Create Booking, Make Payment, Refund/Cancel).

## Key Design Decisions & Architecture

- **Clean Modularity (SOLID):** The application relies on standard layered architecture (Controllers, Services, Repositories). Domain logic is strictly separated from the transport layer.
- **Strategy Pattern (Extensibility):** Core business logic rules are abstracted using the Strategy Pattern to ensure the system is Open for Extension, Closed for Modification:
  - `PaymentStrategy`: Abstracted into `CardPaymentStrategy`, `UpiPaymentStrategy`, and `WalletPaymentStrategy`.
  - `PricingStrategy`: Abstracted into `BasePricingStrategy` and `DynamicPricingStrategy` (prices surge based on proximity to the check-in date).
  - `CancellationPolicy`: Abstracted into `FreeCancellationPolicy` and `PartialRefundPolicy`.
- **Composable Filters for Discovery:** Property searching is driven by a `FilterStrategy` interface. Adding new filters (e.g., `AmenityFilter`, `PriceRangeHighFilter`, `RatingFilter`) requires zero changes to the core search loop—just a new class implementing the interface.
- **Concurrency & Double-Booking Prevention:** Shared inventory is protected against concurrent bookings using fine-grained locking (`ReentrantLock` at the `HotelRoom` level). The `BookingService` applies double-checked locking to prevent Time-Of-Check to Time-Of-Use (TOCTOU) race conditions when multiple users attempt to book the exact same room at the exact same millisecond. 
- **Thread-Safe In-Memory Persistence:** As requested, no production database is used. The DAO layer relies on `ConcurrentHashMap` to allow safe, lock-free reads and granular bucket-level writes across multiple threads.
- **Idempotency:** Payment intent creation leverages an `idempotencyKey` to safely handle network retries without double-charging the user.

## Assumptions

- **Authentication/Authorization:** Handled by an API gateway or middleware in a real environment. Currently, endpoints accept raw `ownerId` and `guestName` strings.
- **Inventory Generation:** When a room is added to a property, the system pre-populates its day-by-day availability for the next 30 days. In production, this would be a rolling window managed by a cron job.
- **Multi-Property Owners:** Rather than hardcoding a "standalone property" vs "hotel chain", all properties belong to an `ownerId`. A single standalone property is just an owner with a single property attached to their ID.
- **Payment & Refunds:** External payment gateways are mocked via logging.

## What I Would Do With More Time

1. **Production Database & Migrations:** Swap out the in-memory DAO layer with Spring Data JPA, Hibernate, and PostgreSQL. Use Flyway or Liquibase for database schema migrations.
2. **Database-Level Locking:** Replace the application-level `ReentrantLock` with JPA Pessimistic Write locks or Optimistic Locking (`@Version`) to safely handle horizontal scaling across multiple application nodes.
3. **OpenAPI / Swagger:** Add SpringDoc OpenAPI to auto-generate interactive API documentation.
4. **Background Scheduled Tasks:** Add Spring `@Scheduled` tasks to:
   - Roll inventory forward automatically every midnight.
   - Automatically cancel and release `PENDING` bookings if the user fails to make a payment within 15 minutes.
5. **Input Validation:** Add `spring-boot-starter-validation` and use `@Valid`, `@NotNull`, `@Min` across all Request DTOs, catching errors neatly in the `GlobalExceptionHandler`.
6. **Pagination & Sorting:** Enhance the `/api/properties/search` endpoint to support Spring `Pageable` for large result sets.

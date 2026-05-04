# Expense Tracker API

A RESTful API for personal financial management, built with Spring Boot 3. The project focuses on handling concurrency, data consistency, and performance optimization for heavy-read/write operations.

## Tech Stack
* **Core:** Java 17, Spring Boot 3, Spring Data JPA
* **Security:** Spring Security, JWT
* **Database & Cache:** MySQL, Redis
* **Infrastructure & Testing:** Docker, Docker Compose, k6 (Load Testing)

## Key Features

**1. Authentication & Security**
* JWT-based authentication with Refresh Token rotation.
* Token blacklist management via Redis.
* Rate limiting implemented to prevent brute-force attacks on auth endpoints.

**2. Concurrency Control & Data Consistency**
* **Shared Wallets:** Applied **Atomic Updates** for deposit transactions to prevent 'Lost Update' anomalies.
* **Budget Management:** Implemented **Optimistic Locking** (`@Version`) with a retry mechanism to handle concurrent wallet modifications safely.
* **Idempotency:** Implemented Idempotency Keys using Redis to guarantee exactly-once processing for transactions during client retries.

**3. Performance Optimization**
* Optimized heavy-read reporting APIs using Redis caching (Cache-Aside pattern).
* Applied MySQL Composite Indexing to eliminate full table scans for dynamic filtering.

## Performance Verification
The system was load-tested using **k6** to verify concurrency handling and response latency under stress:
* **Scenario:** 100 concurrent Virtual Users (VUs) creating transactions and fetching reports continuously for 30 seconds.
* **Result:** Achieved **~130 req/s** throughput with **0.00% error rate** and **p(95) response time < 250ms**.

![k6 Load Test Result](docs/k6-result.png)
## Run Locally

**1. Start dependencies (MySQL, Redis)**
```bash
docker-compose up -d
2. Set environment variables

Properties
DB_USERNAME=root
DB_PASSWORD=yourpassword
JWT_SECRET=yoursecret
3. Run the application

Bash
./mvnw spring-boot:run
API Documentation (Swagger UI): http://localhost:8080/swagger-ui/index.html

# Architecture & Design Document

## Overview
SecureBank Simulator is a full-stack banking application that demonstrates enterprise-level software engineering practices including multi-threaded transaction processing, RESTful API design, and modern frontend development.

## System Architecture

### Three-Tier Architecture

```
┌─────────────────────────────────────────────┐
│          Frontend (React)                    │
│  - React Components                          │
│  - Axios API Client                          │
│  - CSS3 Styling                              │
└──────────────┬──────────────────────────────┘
               │ HTTP/REST
               │
┌──────────────▼──────────────────────────────┐
│       Backend (Spring Boot)                  │
│  ┌─────────────────────────────────────┐   │
│  │  REST Controllers                    │   │
│  │  - AccountController                 │   │
│  │  - TransactionController             │   │
│  └────────────┬────────────────────────┘   │
│               │                              │
│  ┌────────────▼────────────────────────┐   │
│  │  Service Layer                       │   │
│  │  - AccountService                    │   │
│  │  - TransactionService (Multi-thread) │   │
│  └────────────┬────────────────────────┘   │
│               │                              │
│  ┌────────────▼────────────────────────┐   │
│  │  Repository Layer (JDBC)             │   │
│  │  - AccountRepository                 │   │
│  │  - TransactionRepository             │   │
│  └────────────┬────────────────────────┘   │
└───────────────┼──────────────────────────────┘
                │ JDBC
                │
┌───────────────▼──────────────────────────────┐
│       Database (MySQL)                        │
│  - accounts table                             │
│  - transactions table                         │
└───────────────────────────────────────────────┘
```

## Multi-threaded Transaction Engine

### Design Principles

1. **Thread Safety**: Uses `ReentrantLock` with fair scheduling
2. **Deadlock Prevention**: Consistent lock ordering (lower account ID first)
3. **Isolation**: Each transaction is isolated and atomic
4. **Concurrency**: Thread pool of 10 workers using `ExecutorService`

### Transaction Flow

```
Client Request
    ↓
REST Controller
    ↓
Transaction Service
    ↓
Acquire Locks (ordered by account ID)
    ↓
Validate (balance check)
    ↓
Execute Transfer
    ↓
Update Database
    ↓
Release Locks
    ↓
Return Response
```

### Lock Ordering Strategy

```java
// Prevent deadlock by always acquiring locks in order
Account firstLock = fromAccountId < toAccountId ? fromAccount : toAccount;
Account secondLock = fromAccountId < toAccountId ? toAccount : fromAccount;

firstLock.acquireLock();
try {
    secondLock.acquireLock();
    try {
        // Perform transaction
    } finally {
        secondLock.releaseLock();
    }
} finally {
    firstLock.releaseLock();
}
```

## Database Design

### Schema

#### Accounts Table
```sql
CREATE TABLE accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_holder_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    account_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_account_number (account_number),
    INDEX idx_email (email)
);
```

#### Transactions Table
```sql
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    from_account_id BIGINT,
    to_account_id BIGINT,
    amount DECIMAL(15,2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_from_account (from_account_id),
    INDEX idx_to_account (to_account_id),
    INDEX idx_status (status),
    FOREIGN KEY (from_account_id) REFERENCES accounts(id) ON DELETE SET NULL,
    FOREIGN KEY (to_account_id) REFERENCES accounts(id) ON DELETE SET NULL
);
```

### Indexes
- `account_number`: Fast lookup by account number
- `email`: Fast user search
- `from_account_id`, `to_account_id`: Fast transaction history queries
- `status`: Filter transactions by status

## REST API Design

### Endpoint Structure

All endpoints follow RESTful conventions:
- `GET` for retrieval
- `POST` for creation/actions
- `PUT` for updates
- `DELETE` for deletion

### Response Format

Success Response:
```json
{
  "id": 1,
  "accountNumber": "ACC1000001",
  "accountHolderName": "John Doe",
  "balance": 5000.00,
  "accountType": "SAVINGS"
}
```

Error Response:
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 400,
  "error": "Insufficient Funds",
  "message": "Insufficient funds in account 1"
}
```

## Security Considerations

### Current Implementation
1. **Exception Handling**: Global exception handler prevents stack trace leakage
2. **CORS**: Configured for cross-origin requests
3. **SQL Injection**: Prevented using JDBC PreparedStatements
4. **Input Validation**: Amount validation, null checks
5. **Thread Safety**: Lock-based concurrency control

### Future Enhancements
1. Authentication & Authorization (JWT/OAuth2)
2. Rate limiting
3. Encryption at rest and in transit
4. Audit logging
5. HTTPS enforcement

## Performance Optimizations

1. **Connection Pooling**: HikariCP with 20 max connections
2. **Database Indexing**: Key columns indexed for fast queries
3. **Thread Pool**: Reusable threads for transaction processing
4. **Fair Locks**: Prevent thread starvation

## Testing Strategy

### Unit Tests
- Service layer logic
- Transaction processing scenarios
- Exception handling
- Edge cases (insufficient funds, account not found)

### Integration Tests (Future)
- API endpoint testing
- Database integration
- Concurrent transaction testing

### Load Testing (Future)
- Concurrent user simulation
- Transaction throughput measurement
- Deadlock detection

## Deployment Architecture

### Docker Compose Setup

```yaml
services:
  mysql:        # Database container
  backend:      # Spring Boot application
  frontend:     # React + Nginx
```

### Container Communication
- Frontend → Backend: HTTP proxy through Nginx
- Backend → MySQL: JDBC connection
- All containers on private network

## Monitoring & Observability

### Spring Boot Actuator
Enabled endpoints:
- `/actuator/health` - Application health
- `/actuator/info` - Application info
- `/actuator/metrics` - Performance metrics

### Logging
- Transaction logs with timestamps
- Error logs with stack traces
- Service initialization logs

## Data Structures & Algorithms Used

1. **ReentrantLock**: Thread synchronization
2. **ExecutorService Thread Pool**: Task scheduling
3. **HashMap/List**: In-memory caching (Account objects)
4. **B-Tree Indexes**: Database query optimization
5. **Lock Ordering**: Deadlock prevention algorithm

## Scalability Considerations

### Current Limitations
- Single database instance
- Thread pool size: 10
- No horizontal scaling

### Future Improvements
1. Database replication (master-slave)
2. Load balancer for backend instances
3. Redis cache for frequently accessed data
4. Message queue for async operations
5. Microservices architecture

## Code Quality Practices

1. **OOP Principles**: Encapsulation, inheritance, polymorphism
2. **SOLID Principles**: Single responsibility, dependency injection
3. **Design Patterns**: Repository pattern, service layer pattern
4. **Exception Handling**: Custom exceptions, global handler
5. **Documentation**: Comprehensive README, code comments

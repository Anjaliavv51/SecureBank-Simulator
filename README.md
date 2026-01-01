# SecureBank Simulator 🏦

A full-stack, multi-threaded banking simulator built with modern technologies and best practices. This project demonstrates secure OOP principles, concurrent transaction processing, and containerized deployment.

## 🌟 Features

- **Multi-threaded Transaction Engine**: Java ExecutorService with ReentrantLock for thread-safe concurrent operations
- **100+ Sample Accounts**: Pre-populated database for testing and demonstration
- **Robust Exception Handling**: Comprehensive error handling with custom exceptions
- **REST API**: Secure RESTful endpoints with CORS support
- **Modern React Frontend**: Responsive UI with ES6+ JavaScript, HTML5, and CSS3
- **Docker Containerization**: Multi-container deployment with Docker Compose
- **MySQL Database**: Persistent data storage with JDBC
- **Transaction Types**: Support for transfers, deposits, and withdrawals
- **Real-time Updates**: Live balance updates and transaction history

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 2.7.14
- **Language**: Java 11
- **Database**: MySQL 8.0
- **Data Access**: JDBC with JdbcTemplate
- **Build Tool**: Maven
- **Concurrency**: ExecutorService, ReentrantLock
- **API**: RESTful endpoints with JSON

### Frontend
- **Framework**: React 18.2
- **Language**: ES6+ JavaScript
- **Styling**: CSS3 with modern design
- **HTTP Client**: Axios
- **Build Tool**: React Scripts (Webpack)

### DevOps
- **Containerization**: Docker & Docker Compose
- **Web Server**: Nginx (for frontend)
- **API Testing**: Postman Collection included
- **Version Control**: Git/GitHub

## 📋 Prerequisites

- Docker and Docker Compose
- Java 11 (for local development)
- Node.js 18+ (for local frontend development)
- Maven 3.8+ (for local backend development)
- Git

## 🚀 Quick Start

### Using Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/Anjaliavv51/SecureBank-Simulator.git
   cd SecureBank-Simulator
   ```

2. **Start all services**
   ```bash
   docker-compose up --build
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api
   - MySQL: localhost:3306

4. **Stop services**
   ```bash
   docker-compose down
   ```

### Local Development

#### Backend Setup

1. **Navigate to project root**
   ```bash
   cd SecureBank-Simulator
   ```

2. **Install dependencies and build**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   Or run the JAR:
   ```bash
   java -jar target/securebank-simulator-1.0.0.jar
   ```

#### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm start
   ```

4. **Build for production**
   ```bash
   npm run build
   ```

## 📚 API Documentation

### Account Endpoints

- `GET /api/accounts` - Get all accounts
- `GET /api/accounts/{id}` - Get account by ID
- `GET /api/accounts/number/{accountNumber}` - Get account by account number
- `GET /api/accounts/count` - Get total account count
- `POST /api/accounts` - Create new account
- `PUT /api/accounts/{id}` - Update account
- `DELETE /api/accounts/{id}` - Delete account

### Transaction Endpoints

- `GET /api/transactions` - Get all transactions
- `GET /api/transactions/{id}` - Get transaction by ID
- `GET /api/transactions/account/{accountId}` - Get transactions for an account
- `POST /api/transactions/transfer` - Transfer money between accounts
- `POST /api/transactions/deposit` - Deposit money to an account
- `POST /api/transactions/withdraw` - Withdraw money from an account

### Example API Calls

**Transfer Money**
```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": 1,
    "toAccountId": 2,
    "amount": 100.00,
    "description": "Payment"
  }'
```

**Deposit Money**
```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "amount": 500.00,
    "description": "Salary"
  }'
```

## 🧪 Testing with Postman

A Postman collection is included in `postman_collection.json`. Import it into Postman to test all API endpoints.

1. Open Postman
2. Import the collection: `postman_collection.json`
3. Ensure the backend is running on `localhost:8080`
4. Execute requests from the collection

## 🏗️ Architecture

### Multi-threaded Transaction Processing

The application uses a thread-safe transaction engine with the following features:

- **ExecutorService Thread Pool**: 10 concurrent threads for processing transactions
- **ReentrantLock**: Fair locking mechanism to prevent race conditions
- **Deadlock Prevention**: Consistent lock ordering (lower ID first)
- **Exception Handling**: Comprehensive error handling with rollback support
- **Transaction States**: PENDING, COMPLETED, FAILED

### Database Schema

**Accounts Table**
- id (PRIMARY KEY)
- account_number (UNIQUE)
- account_holder_name
- email
- balance (DECIMAL)
- account_type (SAVINGS/CHECKING)
- created_at, updated_at

**Transactions Table**
- id (PRIMARY KEY)
- from_account_id (FOREIGN KEY)
- to_account_id (FOREIGN KEY)
- amount (DECIMAL)
- transaction_type (TRANSFER/DEPOSIT/WITHDRAWAL)
- status (PENDING/COMPLETED/FAILED)
- description
- created_at

## 🔒 Security Features

- Input validation on all endpoints
- Thread-safe concurrent operations
- SQL injection prevention via JDBC PreparedStatements
- CORS configuration for frontend-backend communication
- Exception handling without exposing sensitive data
- Balance validation before transactions

## 📊 Data Structures & Algorithms

- **Concurrent Data Structures**: Thread-safe account management
- **Locking Algorithms**: ReentrantLock with fairness policy
- **Deadlock Avoidance**: Ordered resource allocation
- **Search & Filter**: Efficient database indexing on account numbers and emails

## 🐛 Troubleshooting

### Common Issues

1. **Port already in use**
   ```bash
   # Change ports in docker-compose.yml or application.properties
   ```

2. **Database connection failed**
   ```bash
   # Ensure MySQL container is healthy
   docker-compose logs mysql
   ```

3. **Frontend can't connect to backend**
   ```bash
   # Check REACT_APP_API_URL in frontend/.env
   # Ensure backend is running on port 8080
   ```

## 📈 Future Enhancements

- User authentication and authorization (JWT)
- Account statements and PDF generation
- Email notifications for transactions
- Transaction scheduling
- Multi-currency support
- Admin dashboard
- Audit logging
- Performance metrics

## 👥 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React community for the frontend library
- Docker for containerization
- MySQL for reliable database management

## 📧 Contact

Project Link: [https://github.com/Anjaliavv51/SecureBank-Simulator](https://github.com/Anjaliavv51/SecureBank-Simulator)

---

**Built with ❤️ using Spring Boot, React, Docker, and modern software engineering practices**

# Development Guide

This guide will help you set up your development environment and contribute to the SecureBank Simulator project.

## Prerequisites

- **Java Development Kit (JDK) 11+**
- **Maven 3.8+**
- **Node.js 18+**
- **Docker Desktop** (for containerized development)
- **Git**
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code

## Local Development Setup

### Backend Development

#### 1. Import Project into IDE

**IntelliJ IDEA:**
```bash
File → Open → Select pom.xml → Open as Project
```

**Eclipse:**
```bash
File → Import → Maven → Existing Maven Projects
```

**VS Code:**
```bash
Install "Extension Pack for Java"
Open project folder
```

#### 2. Configure Database

For local development without Docker, install MySQL and create database:

```sql
CREATE DATABASE securebank;
CREATE USER 'bankuser'@'localhost' IDENTIFIED BY 'bankpass123';
GRANT ALL PRIVILEGES ON securebank.* TO 'bankuser'@'localhost';
FLUSH PRIVILEGES;
```

Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/securebank
```

#### 3. Run Backend

**Using Maven:**
```bash
mvn spring-boot:run
```

**Using IDE:**
- Right-click `SecureBankApplication.java`
- Select "Run SecureBankApplication"

Backend will start on: http://localhost:8080

#### 4. Run Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=TransactionServiceTest

# Run with coverage
mvn test jacoco:report
```

### Frontend Development

#### 1. Install Dependencies

```bash
cd frontend
npm install
```

#### 2. Configure API URL

Create `.env.local` file:
```bash
REACT_APP_API_URL=http://localhost:8080/api
```

#### 3. Run Development Server

```bash
npm start
```

Frontend will start on: http://localhost:3000

#### 4. Build for Production

```bash
npm run build
```

Built files will be in `frontend/build/`

## Project Structure

```
SecureBank-Simulator/
├── src/
│   ├── main/
│   │   ├── java/com/securebank/
│   │   │   ├── SecureBankApplication.java    # Main application
│   │   │   ├── model/                         # Domain models
│   │   │   │   ├── Account.java
│   │   │   │   └── Transaction.java
│   │   │   ├── repository/                    # Data access
│   │   │   │   ├── AccountRepository.java
│   │   │   │   └── TransactionRepository.java
│   │   │   ├── service/                       # Business logic
│   │   │   │   ├── AccountService.java
│   │   │   │   └── TransactionService.java
│   │   │   ├── controller/                    # REST endpoints
│   │   │   │   ├── AccountController.java
│   │   │   │   └── TransactionController.java
│   │   │   ├── exception/                     # Exception handling
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── AccountNotFoundException.java
│   │   │   │   ├── InsufficientFundsException.java
│   │   │   │   └── TransactionException.java
│   │   │   └── config/                        # Configuration
│   │   └── resources/
│   │       ├── application.properties
│   │       └── schema.sql
│   └── test/
│       └── java/com/securebank/
│           └── service/
│               └── TransactionServiceTest.java
├── frontend/
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── components/
│   │   │   ├── AccountList.js
│   │   │   ├── TransactionForm.js
│   │   │   └── TransactionHistory.js
│   │   ├── services/
│   │   │   └── api.js
│   │   ├── App.js
│   │   └── index.js
│   └── package.json
├── pom.xml                               # Maven configuration
├── Dockerfile                            # Backend Docker image
├── docker-compose.yml                    # Multi-container setup
├── README.md                             # Project documentation
├── ARCHITECTURE.md                       # Architecture details
└── postman_collection.json              # API tests
```

## Coding Standards

### Java Code Style

#### Naming Conventions
- **Classes**: PascalCase (e.g., `AccountService`)
- **Methods**: camelCase (e.g., `getAccountById`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_THREADS`)
- **Packages**: lowercase (e.g., `com.securebank.service`)

#### Example Service Class
```java
@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> new AccountNotFoundException(
                "Account not found with id: " + id
            ));
    }
}
```

### React Code Style

#### Functional Components
```javascript
const AccountList = ({ onSelectAccount }) => {
  const [accounts, setAccounts] = useState([]);
  
  useEffect(() => {
    loadAccounts();
  }, []);
  
  const loadAccounts = async () => {
    const response = await accountService.getAllAccounts();
    setAccounts(response.data);
  };
  
  return (
    <div className="account-list">
      {/* Component JSX */}
    </div>
  );
};
```

## Testing Guidelines

### Writing Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    
    @Mock
    private AccountRepository accountRepository;
    
    @InjectMocks
    private TransactionService transactionService;
    
    @Test
    void testTransferSuccess() {
        // Arrange
        when(accountRepository.findById(1L))
            .thenReturn(Optional.of(fromAccount));
        
        // Act
        Transaction result = transactionService.processTransaction(
            1L, 2L, new BigDecimal("100.00"), "Test"
        );
        
        // Assert
        assertEquals("COMPLETED", result.getStatus());
    }
}
```

### Test Coverage Goals
- Service layer: >80%
- Controller layer: >70%
- Repository layer: >60%

## API Testing with Postman

### Import Collection
1. Open Postman
2. Click Import
3. Select `postman_collection.json`
4. Collection will appear in sidebar

### Example: Create Transfer
```
POST http://localhost:8080/api/transactions/transfer
Content-Type: application/json

{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 100.00,
  "description": "Test transfer"
}
```

## Database Management

### View Database in MySQL

```bash
# Connect to database
mysql -u bankuser -p securebank

# View tables
SHOW TABLES;

# View accounts
SELECT * FROM accounts LIMIT 10;

# View recent transactions
SELECT * FROM transactions ORDER BY created_at DESC LIMIT 10;
```

### Reset Database

```bash
# Drop and recreate
mysql -u bankuser -p securebank < src/main/resources/schema.sql
```

## Docker Development

### Build Images

```bash
# Build backend
docker build -t securebank-backend .

# Build frontend
docker build -t securebank-frontend frontend/
```

### Run with Docker Compose

```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f

# Stop services
docker compose down

# Remove volumes (reset database)
docker compose down -v
```

### Debug in Container

```bash
# Access backend container
docker compose exec backend sh

# Access database
docker compose exec mysql mysql -u bankuser -p securebank
```

## Common Development Tasks

### Add New REST Endpoint

1. **Create method in Service**
```java
public Account createAccount(Account account) {
    return accountRepository.save(account);
}
```

2. **Add Controller endpoint**
```java
@PostMapping
public ResponseEntity<Account> createAccount(@RequestBody Account account) {
    Account created = accountService.createAccount(account);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

3. **Test with Postman**
4. **Add unit test**

### Add New Frontend Component

1. **Create component file**
```javascript
// src/components/MyComponent.js
const MyComponent = () => {
  return <div>My Component</div>;
};
export default MyComponent;
```

2. **Create CSS file**
```css
/* src/components/MyComponent.css */
.my-component {
  /* styles */
}
```

3. **Import in App.js**
```javascript
import MyComponent from './components/MyComponent';
```

## Troubleshooting

### Backend won't start
```bash
# Check Java version
java -version

# Clean Maven cache
mvn clean install -U

# Check port 8080 is free
lsof -i :8080
```

### Frontend build fails
```bash
# Clear node_modules
rm -rf node_modules package-lock.json
npm install

# Clear cache
npm cache clean --force
```

### Database connection error
```bash
# Verify MySQL is running
docker compose ps

# Check connection
mysql -h localhost -u bankuser -p -e "SELECT 1"
```

## Git Workflow

### Branching Strategy
- `main`: Production-ready code
- `develop`: Development branch
- `feature/*`: New features
- `bugfix/*`: Bug fixes

### Commit Messages
```bash
# Format
<type>: <subject>

# Examples
feat: Add account search functionality
fix: Resolve transaction deadlock issue
docs: Update API documentation
test: Add integration tests for deposits
```

### Pull Request Process
1. Create feature branch
2. Make changes and commit
3. Push to GitHub
4. Create Pull Request
5. Request review
6. Address feedback
7. Merge when approved

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://reactjs.org/docs)
- [Docker Documentation](https://docs.docker.com/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Maven Guide](https://maven.apache.org/guides/)

## Getting Help

- **Issues**: Create GitHub issue
- **Questions**: Start a discussion
- **Documentation**: Check README and ARCHITECTURE.md

# Multi-Repository Banking System PoC

This project demonstrates a Java Maven banking system Proof of Concept (PoC) consisting of multiple interdependent Git repositories. Each repository represents a separate module with clear Maven dependencies.

## Repository Structure

The banking system is organized into four separate modules:

```
banking-system/
├── banking-core/          # Core domain models and interfaces
├── banking-account/        # Account management (depends on banking-core)
├── banking-transaction/    # Transaction processing (depends on banking-core, banking-account)
├── banking-api/           # REST API + Frontend (depends on banking-core, banking-account, banking-transaction)
└── pom.xml                # Parent POM (optional, for unified builds)
```

## Module Dependencies

```
banking-core (no dependencies)
    ↑
    ├── banking-account
    │       ↑
    │       └── banking-transaction
    │               ↑
    │               └── banking-api (includes integrated frontend)
```
```

### Dependency Graph

- **banking-core**: Foundation module with domain models (`Money`, `AccountType`, `TransactionType`) and exceptions
- **banking-account**: Depends on `banking-core`. Provides account management functionality
- **banking-transaction**: Depends on `banking-core` and `banking-account`. Provides transaction processing
- **banking-api**: Depends on `banking-core`, `banking-account`, `banking-transaction`. REST API using Spring Boot with integrated frontend (HTML/CSS/JS files in `src/main/resources/static/`)

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Git (for managing separate repositories)

## Building the System

### Option 1: Build All Modules Together (Using Parent POM)

If you have all modules in the same workspace, you can build everything at once:

```bash
mvn clean install
```

This will build modules in the correct order (core → account → transaction → api).

### Option 2: Build Modules Individually (For Separate Repositories)

When modules are in separate Git repositories, build them in dependency order:

```bash
# 1. Build banking-core first
cd banking-core
mvn clean install
cd ..

# 2. Build banking-account (depends on banking-core)
cd banking-account
mvn clean install
cd ..

# 3. Build banking-transaction (depends on banking-core and banking-account)
cd banking-transaction
mvn clean install
cd ..

# 4. Build banking-api (depends on all)
cd banking-api
mvn clean install
cd ..
```

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Tests for a Specific Module

```bash
cd banking-core
mvn test
```

## Running the Applications

### REST API Server + Frontend

```bash
cd banking-api
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

### Frontend Web Application

The frontend is integrated into the Spring Boot API server. Just start the API:

```bash
cd banking-api
mvn spring-boot:run
```

Then open `http://localhost:8080` in your browser - both the frontend and API are served from the same port!

The frontend files are located in: `banking-api/src/main/resources/static/`

## Maven Dependency Examples

### banking-account/pom.xml

```xml
<dependency>
    <groupId>com.banking</groupId>
    <artifactId>banking-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

### banking-transaction/pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>com.banking</groupId>
        <artifactId>banking-core</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.banking</groupId>
        <artifactId>banking-account</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### banking-api/pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.banking</groupId>
        <artifactId>banking-core</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.banking</groupId>
        <artifactId>banking-account</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.banking</groupId>
        <artifactId>banking-transaction</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## Setting Up as Separate Git Repositories

If you want each module to be a separate Git repository:

1. **Initialize repositories:**
   ```bash
   cd banking-core
   git init
   git add .
   git commit -m "Initial commit: banking-core module"
   
   cd ../banking-account
   git init
   git add .
   git commit -m "Initial commit: banking-account module"
   
   # Repeat for banking-transaction and banking-api
   ```

2. **Push to remote repositories:**
   ```bash
   # For each repository
   git remote add origin <repository-url>
   git push -u origin main
   ```

3. **When working with separate repositories:**
   - Build and install `banking-core` to your local Maven repository
   - Other modules will automatically resolve dependencies from local Maven repository
   - Or use a Maven repository manager (Nexus, Artifactory) for shared dependencies

## Module Details

### banking-core
- **Purpose**: Core domain models and shared interfaces
- **Contents**: `Money`, `AccountType`, `TransactionType`, exceptions
- **Dependencies**: None (except JUnit for testing)

### banking-account
- **Purpose**: Account management functionality
- **Contents**: `Account` domain model, `AccountService`
- **Dependencies**: `banking-core`

### banking-transaction
- **Purpose**: Transaction processing
- **Contents**: `Transaction` domain model, `TransactionService`
- **Dependencies**: `banking-core`, `banking-account`

### banking-api
- **Purpose**: REST API server + Integrated Frontend
- **Contents**: Spring Boot REST controllers, DTOs, static frontend files (HTML/CSS/JS)
- **Dependencies**: `banking-core`, `banking-account`, `banking-transaction`
- **Technology**: Spring Boot 2.7.18
- **Frontend Location**: `src/main/resources/static/`


## Testing Strategy

Each module includes unit tests:
- `banking-core`: Tests for `Money` value object
- `banking-account`: Tests for `Account` and `AccountService`
- `banking-transaction`: Tests for `TransactionService`

Run tests with:
```bash
mvn test
```

## Architecture Highlights

1. **Modular Design**: Each module has a clear responsibility
2. **Dependency Management**: Clear dependency hierarchy prevents circular dependencies
3. **Code Reuse**: Core domain models are shared across modules
4. **Testability**: Each module can be tested independently
5. **Scalability**: Easy to add new modules following the same pattern

## Future Enhancements

Potential additions to the PoC:
- REST API layer module
- Database persistence module
- Security/authentication module
- Reporting module
- Notification module

## Troubleshooting

### Build Failures

If a module fails to build:
1. Ensure dependencies are built and installed first
2. Check that Maven can resolve dependencies: `mvn dependency:resolve`
3. Verify Java version: `java -version` (should be 11+)

### Dependency Resolution Issues

If Maven cannot find dependencies:
1. Ensure parent modules are installed: `mvn install` in dependency order
2. Check local repository: `~/.m2/repository/com/banking/`
3. Clear and rebuild: `mvn clean install`

## License

This is a Proof of Concept project for educational purposes.


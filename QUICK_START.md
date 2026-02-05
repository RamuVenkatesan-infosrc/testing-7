# Quick Start Guide

## Prerequisites
- Java 11+
- Maven 3.6+

## Build Order (Important!)

Build modules in this exact order:

```bash
# 1. Core (no dependencies)
cd banking-core
mvn clean install

# 2. Account (depends on core)
cd ../banking-account
mvn clean install

# 3. Transaction (depends on core + account)
cd ../banking-transaction
mvn clean install

# 4. API (depends on all)
cd ../banking-api
mvn clean install
```

## Quick Build (All at Once)

**Windows:**
```bash
.\build.bat
```

**Linux/Mac:**
```bash
chmod +x build.sh
./build.sh
```

Or use the parent POM:
```bash
mvn clean install
```

## Run Application

**IMPORTANT**: Build all dependencies first (see Build Order above)!

### REST API Server + Frontend

```bash
cd banking-api
mvn spring-boot:run
```

API will be available at: `http://localhost:8080`

### Frontend Web App

The frontend is integrated into the Spring Boot API. Just start the API and open `http://localhost:8080`!

## Test

```bash
# Test all modules
mvn test

# Test specific module
cd banking-core
mvn test
```

## Module Dependencies

```
banking-core (standalone)
    ↓
banking-account
    ↓
banking-transaction
    ↓
banking-api (REST API + Frontend)
```

## Repository Structure

Each module can be a separate Git repository:
- `banking-core/` → Git repo 1
- `banking-account/` → Git repo 2
- `banking-transaction/` → Git repo 3
- `banking-api/` → Git repo 4 (includes frontend in `src/main/resources/static/`)

When working with separate repos, install dependencies to local Maven repo before building dependents.


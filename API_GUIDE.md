# Banking API Guide

## Overview

The Banking API is a RESTful web service built with Spring Boot that provides endpoints for account and transaction management.

## Base URL

```
http://localhost:8080/api
```

## Endpoints

### Account Endpoints

#### Create Account
```
POST /api/accounts
Content-Type: application/json

{
  "customerId": "CUST001",
  "accountType": "SAVINGS",
  "initialBalance": 1000.0,
  "currency": "USD"
}
```

**Response:**
```json
{
  "accountId": "uuid-here",
  "customerId": "CUST001",
  "accountType": "SAVINGS",
  "balance": 1000.0,
  "currency": "USD",
  "active": true
}
```

#### Get All Accounts
```
GET /api/accounts
```

#### Get Account by ID
```
GET /api/accounts/{accountId}
```

#### Get Accounts by Customer
```
GET /api/accounts/customer/{customerId}
```

#### Get Account Balance
```
GET /api/accounts/{accountId}/balance
```

### Transaction Endpoints

#### Deposit
```
POST /api/transactions/deposit
Content-Type: application/json

{
  "accountId": "account-id-here",
  "amount": 200.0,
  "currency": "USD",
  "description": "Salary deposit"
}
```

#### Withdraw
```
POST /api/transactions/withdraw
Content-Type: application/json

{
  "accountId": "account-id-here",
  "amount": 150.0,
  "currency": "USD",
  "description": "ATM withdrawal"
}
```

#### Transfer
```
POST /api/transactions/transfer
Content-Type: application/json

{
  "fromAccountId": "account-id-1",
  "toAccountId": "account-id-2",
  "amount": 100.0,
  "currency": "USD",
  "description": "Payment"
}
```

#### Get Transaction History
```
GET /api/transactions/account/{accountId}
```

#### Get Transaction by ID
```
GET /api/transactions/{transactionId}
```

## Example cURL Commands

### Create Account
```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "accountType": "SAVINGS",
    "initialBalance": 1000.0,
    "currency": "USD"
  }'
```

### Deposit
```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "account-id-here",
    "amount": 200.0,
    "currency": "USD",
    "description": "Salary deposit"
  }'
```

### Get All Accounts
```bash
curl http://localhost:8080/api/accounts
```

## Error Responses

The API returns standard HTTP status codes:
- `200 OK` - Success
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request data
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## CORS

The API is configured to accept requests from any origin (CORS enabled) for development purposes.


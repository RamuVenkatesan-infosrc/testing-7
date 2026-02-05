# Testing the Banking API

## API Base URL
```
http://localhost:8080/api
```

## Method 1: Browser Testing (GET requests only)

Open these URLs in your browser:

1. **Get all accounts:**
   ```
   http://localhost:8080/api/accounts
   ```

2. **Get account by ID** (replace `{accountId}` with actual ID):
   ```
   http://localhost:8080/api/accounts/{accountId}
   ```

3. **Get customer accounts** (replace `{customerId}` with actual ID):
   ```
   http://localhost:8080/api/accounts/customer/{customerId}
   ```

## Method 2: Using cURL (Command Line)

### Create an Account
```bash
curl -X POST http://localhost:8080/api/accounts ^
  -H "Content-Type: application/json" ^
  -d "{\"customerId\":\"CUST001\",\"accountType\":\"SAVINGS\",\"initialBalance\":1000.0,\"currency\":\"USD\"}"
```

### Get All Accounts
```bash
curl http://localhost:8080/api/accounts
```

### Deposit Money
```bash
curl -X POST http://localhost:8080/api/transactions/deposit ^
  -H "Content-Type: application/json" ^
  -d "{\"accountId\":\"YOUR_ACCOUNT_ID\",\"amount\":200.0,\"currency\":\"USD\",\"description\":\"Salary deposit\"}"
```

### Withdraw Money
```bash
curl -X POST http://localhost:8080/api/transactions/withdraw ^
  -H "Content-Type: application/json" ^
  -d "{\"accountId\":\"YOUR_ACCOUNT_ID\",\"amount\":100.0,\"currency\":\"USD\",\"description\":\"ATM withdrawal\"}"
```

### Transfer Money
```bash
curl -X POST http://localhost:8080/api/transactions/transfer ^
  -H "Content-Type: application/json" ^
  -d "{\"fromAccountId\":\"ACCOUNT_1\",\"toAccountId\":\"ACCOUNT_2\",\"amount\":50.0,\"currency\":\"USD\",\"description\":\"Payment\"}"
```

### Get Transaction History
```bash
curl http://localhost:8080/api/transactions/account/YOUR_ACCOUNT_ID
```

## Method 3: Using PowerShell (Windows)

### Create an Account
```powershell
$body = @{
    customerId = "CUST001"
    accountType = "SAVINGS"
    initialBalance = 1000.0
    currency = "USD"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/accounts" -Method Post -Body $body -ContentType "application/json"
```

### Get All Accounts
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/accounts" -Method Get
```

### Deposit Money
```powershell
$body = @{
    accountId = "YOUR_ACCOUNT_ID"
    amount = 200.0
    currency = "USD"
    description = "Salary deposit"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/transactions/deposit" -Method Post -Body $body -ContentType "application/json"
```

## Method 4: Using the Frontend

1. Start the frontend server:
   ```bash
   cd banking-frontend/src/main/webapp
   python -m http.server 8081
   ```

2. Open browser: `http://localhost:8081`

3. Use the web interface to:
   - Create accounts
   - View accounts
   - Make deposits/withdrawals
   - Transfer money
   - View transaction history

## Method 5: Using Postman or Similar Tools

1. **Create Account:**
   - Method: POST
   - URL: `http://localhost:8080/api/accounts`
   - Headers: `Content-Type: application/json`
   - Body (JSON):
     ```json
     {
       "customerId": "CUST001",
       "accountType": "SAVINGS",
       "initialBalance": 1000.0,
       "currency": "USD"
     }
     ```

2. **Get All Accounts:**
   - Method: GET
   - URL: `http://localhost:8080/api/accounts`

3. **Deposit:**
   - Method: POST
   - URL: `http://localhost:8080/api/transactions/deposit`
   - Headers: `Content-Type: application/json`
   - Body (JSON):
     ```json
     {
       "accountId": "YOUR_ACCOUNT_ID",
       "amount": 200.0,
       "currency": "USD",
       "description": "Salary deposit"
     }
     ```

## Quick Test Sequence

1. **Create an account:**
   ```bash
   curl -X POST http://localhost:8080/api/accounts -H "Content-Type: application/json" -d "{\"customerId\":\"CUST001\",\"accountType\":\"SAVINGS\",\"initialBalance\":1000.0,\"currency\":\"USD\"}"
   ```

2. **Copy the accountId from the response**

3. **Make a deposit:**
   ```bash
   curl -X POST http://localhost:8080/api/transactions/deposit -H "Content-Type: application/json" -d "{\"accountId\":\"PASTE_ACCOUNT_ID_HERE\",\"amount\":200.0,\"currency\":\"USD\",\"description\":\"Test deposit\"}"
   ```

4. **View all accounts:**
   ```bash
   curl http://localhost:8080/api/accounts
   ```

5. **View transaction history:**
   ```bash
   curl http://localhost:8080/api/transactions/account/PASTE_ACCOUNT_ID_HERE
   ```

## Expected Response Format

### Account Response
```json
{
  "accountId": "uuid-string",
  "customerId": "CUST001",
  "accountType": "SAVINGS",
  "balance": 1000.0,
  "currency": "USD",
  "active": true
}
```

### Transaction Response
```json
{
  "transactionId": "uuid-string",
  "accountId": "account-uuid",
  "type": "DEPOSIT",
  "amount": 200.0,
  "currency": "USD",
  "timestamp": "2026-01-14T10:54:27.123",
  "description": "Salary deposit",
  "relatedAccountId": null
}
```


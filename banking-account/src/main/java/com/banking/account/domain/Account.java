package com.banking.account.domain;

import com.banking.core.domain.AccountType;
import com.banking.core.domain.Money;
import com.banking.core.exception.InsufficientFundsException;
import com.banking.core.exception.InvalidAccountException;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a bank account with balance and account details.
 */
public class Account {
    private final String accountId;
    private final String customerId;
    private final AccountType accountType;
    private Money balance;
    private boolean active;

    public Account(String customerId, AccountType accountType, Money initialBalance) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (accountType == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }
        if (initialBalance == null) {
            throw new IllegalArgumentException("Initial balance cannot be null");
        }
        
        this.accountId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = initialBalance;
        this.active = true;
    }

    public Account(String accountId, String customerId, AccountType accountType, Money balance, boolean active) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.active = active;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Money getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public void deposit(Money amount) {
        validateAccount();
        if (amount == null || amount.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(Money amount) {
        validateAccount();
        if (amount == null || amount.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (this.balance.isLessThan(amount)) {
            throw new InsufficientFundsException(
                String.format("Insufficient funds. Balance: %s, Requested: %s", balance, amount)
            );
        }
        this.balance = this.balance.subtract(amount);
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    private void validateAccount() {
        if (!active) {
            throw new InvalidAccountException("Account " + accountId + " is not active");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountId, account.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }

    @Override
    public String toString() {
        return String.format("Account{id='%s', customerId='%s', type=%s, balance=%s, active=%s}",
                accountId, customerId, accountType, balance, active);
    }
}


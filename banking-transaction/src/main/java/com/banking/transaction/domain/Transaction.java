package com.banking.transaction.domain;

import com.banking.core.domain.Money;
import com.banking.core.domain.TransactionType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a banking transaction.
 */
public class Transaction {
    private final String transactionId;
    private final String accountId;
    private final TransactionType type;
    private final Money amount;
    private final LocalDateTime timestamp;
    private final String description;
    private String relatedAccountId; // For transfer transactions

    public Transaction(String accountId, TransactionType type, Money amount, String description) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        
        this.transactionId = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.description = description != null ? description : "";
    }

    public Transaction(String accountId, TransactionType type, Money amount, String description, String relatedAccountId) {
        this(accountId, type, amount, description);
        this.relatedAccountId = relatedAccountId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getRelatedAccountId() {
        return relatedAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return String.format("Transaction{id='%s', accountId='%s', type=%s, amount=%s, timestamp=%s, description='%s'}",
                transactionId, accountId, type, amount, timestamp, description);
    }
}


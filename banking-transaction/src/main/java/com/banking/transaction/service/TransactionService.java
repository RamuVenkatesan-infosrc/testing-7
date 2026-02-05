package com.banking.transaction.service;

import com.banking.account.domain.Account;
import com.banking.account.service.AccountService;
import com.banking.core.domain.Money;
import com.banking.core.domain.TransactionType;
import com.banking.core.exception.InsufficientFundsException;
import com.banking.transaction.domain.Transaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for processing banking transactions.
 */
public class TransactionService {
    private final AccountService accountService;
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    public TransactionService(AccountService accountService) {
        if (accountService == null) {
            throw new IllegalArgumentException("AccountService cannot be null");
        }
        this.accountService = accountService;
    }

    public Transaction deposit(String accountId, Money amount, String description) {
        Account account = accountService.getAccount(accountId);
        account.deposit(amount);
        Transaction transaction = new Transaction(accountId, TransactionType.DEPOSIT, amount, description);
        transactions.put(transaction.getTransactionId(), transaction);
        return transaction;
    }

    public Transaction withdraw(String accountId, Money amount, String description) {
        Account account = accountService.getAccount(accountId);
        account.withdraw(amount);
        Transaction transaction = new Transaction(accountId, TransactionType.WITHDRAWAL, amount, description);
        transactions.put(transaction.getTransactionId(), transaction);
        return transaction;
    }

    public Transaction transfer(String fromAccountId, String toAccountId, Money amount, String description) {
        Account fromAccount = accountService.getAccount(fromAccountId);
        Account toAccount = accountService.getAccount(toAccountId);

        // Withdraw from source account
        fromAccount.withdraw(amount);
        
        // Deposit to destination account
        toAccount.deposit(amount);

        // Create transaction record
        Transaction transaction = new Transaction(
            fromAccountId, 
            TransactionType.TRANSFER, 
            amount, 
            description,
            toAccountId
        );
        transactions.put(transaction.getTransactionId(), transaction);
        return transaction;
    }

    public List<Transaction> getTransactionsByAccount(String accountId) {
        return transactions.values().stream()
                .filter(t -> t.getAccountId().equals(accountId) || 
                           (t.getRelatedAccountId() != null && t.getRelatedAccountId().equals(accountId)))
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public Transaction getTransaction(String transactionId) {
        Transaction transaction = transactions.get(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found: " + transactionId);
        }
        return transaction;
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions.values());
    }

    public Money calculateAccountBalance(String accountId) {
        Account account = accountService.getAccount(accountId);
        return account.getBalance();
    }
}


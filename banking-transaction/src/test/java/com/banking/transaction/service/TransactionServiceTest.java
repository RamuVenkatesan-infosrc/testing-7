package com.banking.transaction.service;

import com.banking.account.domain.Account;
import com.banking.account.service.AccountService;
import com.banking.core.domain.AccountType;
import com.banking.core.domain.Money;
import com.banking.core.domain.TransactionType;
import com.banking.core.exception.InsufficientFundsException;
import com.banking.transaction.domain.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    private AccountService accountService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
        transactionService = new TransactionService(accountService);
    }

    @Test
    void testDeposit() {
        Account account = accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        Transaction transaction = transactionService.deposit(account.getAccountId(), new Money(50.0, "USD"), "Test deposit");
        
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(150.0, account.getBalance().getAmount().doubleValue());
    }

    @Test
    void testWithdraw() {
        Account account = accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        Transaction transaction = transactionService.withdraw(account.getAccountId(), new Money(30.0, "USD"), "Test withdrawal");
        
        assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
        assertEquals(70.0, account.getBalance().getAmount().doubleValue());
    }

    @Test
    void testWithdrawInsufficientFunds() {
        Account account = accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        assertThrows(InsufficientFundsException.class, () -> 
            transactionService.withdraw(account.getAccountId(), new Money(150.0, "USD"), "Test withdrawal")
        );
    }

    @Test
    void testTransfer() {
        Account fromAccount = accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        Account toAccount = accountService.createAccount("CUST002", AccountType.CHECKING, new Money(50.0, "USD"));
        
        Transaction transaction = transactionService.transfer(
            fromAccount.getAccountId(), 
            toAccount.getAccountId(), 
            new Money(40.0, "USD"), 
            "Transfer test"
        );
        
        assertEquals(TransactionType.TRANSFER, transaction.getType());
        assertEquals(60.0, fromAccount.getBalance().getAmount().doubleValue());
        assertEquals(90.0, toAccount.getBalance().getAmount().doubleValue());
        assertEquals(toAccount.getAccountId(), transaction.getRelatedAccountId());
    }

    @Test
    void testGetTransactionsByAccount() {
        Account account = accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        transactionService.deposit(account.getAccountId(), new Money(50.0, "USD"), "Deposit 1");
        transactionService.withdraw(account.getAccountId(), new Money(30.0, "USD"), "Withdrawal 1");
        
        var transactions = transactionService.getTransactionsByAccount(account.getAccountId());
        assertEquals(2, transactions.size());
    }
}


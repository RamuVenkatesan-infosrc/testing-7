package com.banking.account.service;

import com.banking.account.domain.Account;
import com.banking.core.domain.AccountType;
import com.banking.core.domain.Money;
import com.banking.core.exception.InvalidAccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void testCreateAccount() {
        Account account = accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        assertNotNull(account);
        assertNotNull(account.getAccountId());
        assertEquals("CUST001", account.getCustomerId());
    }

    @Test
    void testGetAccount() {
        Account created = accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        Account retrieved = accountService.getAccount(created.getAccountId());
        assertEquals(created.getAccountId(), retrieved.getAccountId());
    }

    @Test
    void testGetNonExistentAccount() {
        assertThrows(InvalidAccountException.class, () -> accountService.getAccount("NON_EXISTENT"));
    }

    @Test
    void testGetAccountsByCustomer() {
        accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        accountService.createAccount("CUST001", AccountType.CHECKING, new Money(200.0, "USD"));
        accountService.createAccount("CUST002", AccountType.SAVINGS, new Money(300.0, "USD"));

        var customerAccounts = accountService.getAccountsByCustomer("CUST001");
        assertEquals(2, customerAccounts.size());
    }
}


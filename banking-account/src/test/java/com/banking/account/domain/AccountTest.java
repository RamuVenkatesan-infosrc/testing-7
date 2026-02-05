package com.banking.account.domain;

import com.banking.core.domain.AccountType;
import com.banking.core.domain.Money;
import com.banking.core.exception.InsufficientFundsException;
import com.banking.core.exception.InvalidAccountException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testAccountCreation() {
        Account account = new Account("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        assertEquals("CUST001", account.getCustomerId());
        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertEquals(100.0, account.getBalance().getAmount().doubleValue());
        assertTrue(account.isActive());
    }

    @Test
    void testDeposit() {
        Account account = new Account("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        account.deposit(new Money(50.0, "USD"));
        assertEquals(150.0, account.getBalance().getAmount().doubleValue());
    }

    @Test
    void testWithdraw() {
        Account account = new Account("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        account.withdraw(new Money(30.0, "USD"));
        assertEquals(70.0, account.getBalance().getAmount().doubleValue());
    }

    @Test
    void testInsufficientFunds() {
        Account account = new Account("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(new Money(150.0, "USD")));
    }

    @Test
    void testDeactivatedAccount() {
        Account account = new Account("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"));
        account.deactivate();
        assertFalse(account.isActive());
        assertThrows(InvalidAccountException.class, () -> account.deposit(new Money(50.0, "USD")));
    }
}


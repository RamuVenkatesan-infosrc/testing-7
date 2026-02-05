package com.banking.account.service;

import com.banking.account.domain.Account;
import com.banking.core.domain.AccountType;
import com.banking.core.domain.Money;
import com.banking.core.exception.InvalidAccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import javax.servlet.http.HttpServletRequest;

class AccountServiceTest {

    private AccountService accountService;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
        mockRequest = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    void testCreateAccount() {
        Mockito.when(mockRequest.getHeader("Origin")).thenReturn("https://trusted-origin.com");
        Account account = accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"), mockRequest);
        assertNotNull(account);
        assertNotNull(account.getAccountId());
        assertEquals("CUST001", account.getCustomerId());
    }

    @Test
    void testCreateAccountUnauthorizedOrigin() {
        Mockito.when(mockRequest.getHeader("Origin")).thenReturn("https://untrusted-origin.com");
        assertThrows(SecurityException.class, () -> accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"), mockRequest));
    }

    @Test
    void testGetAccount() {
        Mockito.when(mockRequest.getHeader("Origin")).thenReturn("https://trusted-origin.com");
        Account created = accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"), mockRequest);
        Account retrieved = accountService.getAccount(created.getAccountId(), mockRequest);
        assertEquals(created.getAccountId(), retrieved.getAccountId());
    }

    @Test
    void testGetAccountUnauthorizedOrigin() {
        Mockito.when(mockRequest.getHeader("Origin")).thenReturn("https://untrusted-origin.com");
        assertThrows(SecurityException.class, () -> accountService.getAccount("ACCOUNT_ID", mockRequest));
    }

    @Test
    void testGetNonExistentAccount() {
        Mockito.when(mockRequest.getHeader("Origin")).thenReturn("https://trusted-origin.com");
        assertThrows(InvalidAccountException.class, () -> accountService.getAccount("NON_EXISTENT", mockRequest));
    }

    @Test
    void testGetAccountsByCustomer() {
        Mockito.when(mockRequest.getHeader("Origin")).thenReturn("https://trusted-origin.com");
        accountService.createAccount("CUST001", AccountType.SAVINGS, new Money(100.0, "USD"), mockRequest);
        accountService.createAccount("CUST001", AccountType.CHECKING, new Money(200.0, "USD"), mockRequest);
        accountService.createAccount("CUST002", AccountType.SAVINGS, new Money(300.0, "USD"), mockRequest);

        var customerAccounts = accountService.getAccountsByCustomer("CUST001", mockRequest);
        assertEquals(2, customerAccounts.size());
    }

    @Test
    void testGetAccountsByCustomerUnauthorizedOrigin() {
        Mockito.when(mockRequest.getHeader("Origin")).thenReturn("https://untrusted-origin.com");
        assertThrows(SecurityException.class, () -> accountService.getAccountsByCustomer("CUST001", mockRequest));
    }
}
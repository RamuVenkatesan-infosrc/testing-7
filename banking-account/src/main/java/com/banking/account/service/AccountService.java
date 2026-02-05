package com.banking.account.service;

import com.banking.account.domain.Account;
import com.banking.core.domain.AccountType;
import com.banking.core.domain.Money;
import com.banking.core.exception.InvalidAccountException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing bank accounts.
 */
public class AccountService {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public Account createAccount(String customerId, AccountType accountType, Money initialBalance) {
        Account account = new Account(customerId, accountType, initialBalance);
        accounts.put(account.getAccountId(), account);
        return account;
    }

    public Account getAccount(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new InvalidAccountException("Account not found: " + accountId);
        }
        return account;
    }

    public List<Account> getAccountsByCustomer(String customerId) {
        List<Account> customerAccounts = new ArrayList<>();
        for (Account account : accounts.values()) {
            if (account.getCustomerId().equals(customerId)) {
                customerAccounts.add(account);
            }
        }
        return customerAccounts;
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public void deactivateAccount(String accountId) {
        Account account = getAccount(accountId);
        account.deactivate();
    }

    public void activateAccount(String accountId) {
        Account account = getAccount(accountId);
        account.activate();
    }

    public Money getBalance(String accountId) {
        Account account = getAccount(accountId);
        return account.getBalance();
    }
}


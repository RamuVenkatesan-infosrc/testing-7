package com.banking.api.controller;

import com.banking.account.domain.Account;
import com.banking.account.service.AccountService;
import com.banking.api.dto.AccountCreateRequest;
import com.banking.api.dto.AccountResponse;
import com.banking.core.domain.AccountType;
import com.banking.core.domain.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final List<String> allowedOrigins = Arrays.asList("https://trusted-origin1.com", "https://trusted-origin2.com");

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    private boolean isValidOrigin(String origin) {
        return allowedOrigins.contains(origin);
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountCreateRequest request, @RequestHeader("Origin") String origin) {
        if (!isValidOrigin(origin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Account account = accountService.createAccount(
            request.getCustomerId(),
            AccountType.valueOf(request.getAccountType()),
            new Money(request.getInitialBalance(), request.getCurrency())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(account));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountId, @RequestHeader("Origin") String origin) {
        if (!isValidOrigin(origin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Account account = accountService.getAccount(accountId);
        return ResponseEntity.ok(toResponse(account));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer(@PathVariable String customerId, @RequestHeader("Origin") String origin) {
        if (!isValidOrigin(origin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Account> accounts = accountService.getAccountsByCustomer(customerId);
        List<AccountResponse> responses = accounts.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts(@RequestHeader("Origin") String origin) {
        if (!isValidOrigin(origin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Account> accounts = accountService.getAllAccounts();
        List<AccountResponse> responses = accounts.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Money> getBalance(@PathVariable String accountId, @RequestHeader("Origin") String origin) {
        if (!isValidOrigin(origin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Money balance = accountService.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    private AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setAccountId(StringEscapeUtils.escapeHtml4(account.getAccountId()));
        response.setCustomerId(StringEscapeUtils.escapeHtml4(account.getCustomerId()));
        response.setAccountType(StringEscapeUtils.escapeHtml4(account.getAccountType().name()));
        response.setBalance(account.getBalance().getAmount().doubleValue());
        response.setCurrency(StringEscapeUtils.escapeHtml4(account.getBalance().getCurrency()));
        response.setActive(account.isActive());
        return response;
    }
}
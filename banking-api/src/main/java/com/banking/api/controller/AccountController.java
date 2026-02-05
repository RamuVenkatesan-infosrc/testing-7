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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountCreateRequest request, HttpServletRequest servletRequest) {
        String csrfToken = servletRequest.getHeader("X-CSRF-TOKEN");
        if (csrfToken == null || !validateCsrfToken(csrfToken)) {
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
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountId, HttpServletResponse response) {
        Account account = accountService.getAccount(accountId);
        String csrfToken = generateCsrfToken();
        response.setHeader("X-CSRF-TOKEN", csrfToken);
        return ResponseEntity.ok(toResponse(account));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer(@PathVariable String customerId, HttpServletResponse response) {
        List<Account> accounts = accountService.getAccountsByCustomer(customerId);
        List<AccountResponse> responses = accounts.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        String csrfToken = generateCsrfToken();
        response.setHeader("X-CSRF-TOKEN", csrfToken);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts(HttpServletResponse response) {
        List<Account> accounts = accountService.getAllAccounts();
        List<AccountResponse> responses = accounts.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        String csrfToken = generateCsrfToken();
        response.setHeader("X-CSRF-TOKEN", csrfToken);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Money> getBalance(@PathVariable String accountId, HttpServletResponse response) {
        Money balance = accountService.getBalance(accountId);
        String csrfToken = generateCsrfToken();
        response.setHeader("X-CSRF-TOKEN", csrfToken);
        return ResponseEntity.ok(balance);
    }

    private AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getAccountId());
        response.setCustomerId(account.getCustomerId());
        response.setAccountType(account.getAccountType().name());
        response.setBalance(account.getBalance().getAmount().doubleValue());
        response.setCurrency(account.getBalance().getCurrency());
        response.setActive(account.isActive());
        return response;
    }

    private String generateCsrfToken() {
        // Implementation will be provided in WebConfig.java
        return "dummy-csrf-token";
    }

    private boolean validateCsrfToken(String token) {
        // Implementation will be provided in WebConfig.java
        return true;
    }
}
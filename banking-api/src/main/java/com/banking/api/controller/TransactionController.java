package com.banking.api.controller;

import com.banking.api.dto.TransactionRequest;
import com.banking.api.dto.TransactionResponse;
import com.banking.core.domain.Money;
import com.banking.transaction.domain.Transaction;
import com.banking.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final List<String> allowedOrigins = List.of("https://trusted-domain.com", "https://another-trusted-domain.com");

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionRequest request, HttpServletRequest servletRequest) {
        if (!isValidOrigin(servletRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Transaction transaction = transactionService.deposit(
            sanitizeInput(request.getAccountId()),
            new Money(request.getAmount(), request.getCurrency()),
            sanitizeInput(request.getDescription())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transaction));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody TransactionRequest request, HttpServletRequest servletRequest) {
        if (!isValidOrigin(servletRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Transaction transaction = transactionService.withdraw(
            sanitizeInput(request.getAccountId()),
            new Money(request.getAmount(), request.getCurrency()),
            sanitizeInput(request.getDescription())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transaction));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@RequestBody TransactionRequest request, HttpServletRequest servletRequest) {
        if (!isValidOrigin(servletRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Transaction transaction = transactionService.transfer(
            sanitizeInput(request.getFromAccountId()),
            sanitizeInput(request.getToAccountId()),
            new Money(request.getAmount(), request.getCurrency()),
            sanitizeInput(request.getDescription())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transaction));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable String accountId, HttpServletRequest servletRequest) {
        if (!isValidOrigin(servletRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Transaction> transactions = transactionService.getTransactionsByAccount(sanitizeInput(accountId));
        List<TransactionResponse> responses = transactions.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable String transactionId, HttpServletRequest servletRequest) {
        if (!isValidOrigin(servletRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Transaction transaction = transactionService.getTransaction(sanitizeInput(transactionId));
        return ResponseEntity.ok(toResponse(transaction));
    }

    private TransactionResponse toResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(sanitizeOutput(transaction.getTransactionId()));
        response.setAccountId(sanitizeOutput(transaction.getAccountId()));
        response.setType(sanitizeOutput(transaction.getType().name()));
        response.setAmount(transaction.getAmount().getAmount().doubleValue());
        response.setCurrency(sanitizeOutput(transaction.getAmount().getCurrency()));
        response.setTimestamp(sanitizeOutput(transaction.getTimestamp().toString()));
        response.setDescription(sanitizeOutput(transaction.getDescription()));
        response.setRelatedAccountId(sanitizeOutput(transaction.getRelatedAccountId()));
        return response;
    }

    private boolean isValidOrigin(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        return origin != null && allowedOrigins.contains(origin);
    }

    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[<>\"'&]", "");
    }

    private String sanitizeOutput(String output) {
        if (output == null) {
            return null;
        }
        return output.replace("&", "&amp;")
                     .replace("<", "&lt;")
                     .replace(">", "&gt;")
                     .replace("\"", "&quot;")
                     .replace("'", "&#x27;");
    }
}
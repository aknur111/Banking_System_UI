package com.example.bank.api.rest;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.service.AccountService;
import com.example.bank.domain.transaction.model.Transaction;
import com.example.bank.domain.transaction.service.TransactionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        Account created = accountService.createAccount(
                request.getCustomerId(),
                request.getCurrency()
        );

        return ResponseEntity
                .created(URI.create("/api/accounts/" + created.getId()))
                .body(created);
    }

    @GetMapping("/customer/{customerId}")
    public List<Account> getCustomerAccounts(@PathVariable Long customerId) {
        return accountService.getCustomerAccounts(customerId);
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @PostMapping("/{id}/withdraw")
    public Account withdraw(@PathVariable Long id, @RequestBody AmountRequest request) {
        return accountService.withdraw(id, request.getAmount());
    }

    @PostMapping("/{id}/deposit")
    public Transaction deposit(@PathVariable Long id,
                               @RequestBody DepositRequest request) {

        return transactionService.depositToAccount(
                id,
                request.getAmount(),
                request.getCurrency(),
                request.getDescription()
        );
    }

    @Data
    public static class CreateAccountRequest {
        private Long customerId;
        private String currency;
    }

    @Data
    public static class AmountRequest {
        private BigDecimal amount;
    }

    @Data
    public static class DepositRequest {
        private BigDecimal amount;
        private String currency;
        private String description;
    }
}

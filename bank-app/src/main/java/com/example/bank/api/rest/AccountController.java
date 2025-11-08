package com.example.bank.api.rest;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.service.AccountService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.net.URI;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        Account created = accountService.createAccount(
                request.getCustomerId(),
                request.getCurrency()
        );

        return ResponseEntity.created(URI.create("/api/accounts/" + created.getId())).body(created);
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



    @Data
    public static class CreateAccountRequest {
        private Long customerId;
        private String currency;
    }
    @Data
    public static class AmountRequest {
        private BigDecimal amount;
    }
}
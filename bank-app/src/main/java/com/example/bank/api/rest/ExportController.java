package com.example.bank.api.rest;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.repository.AccountRepository;
import com.example.bank.domain.export.json.TransactionJsonExporter;
import com.example.bank.domain.transaction.model.Transaction;
import com.example.bank.domain.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final TransactionJsonExporter transactionJsonExporter;

    @GetMapping(value = "/accounts/{accountId}/transactions", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> exportAccountTransactions(@PathVariable Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        List<Transaction> txs = transactionService.getAccountTransactions(accountId);

        String json = transactionJsonExporter.exportAccountTransactions(account, txs);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=account-" + accountId + "-transactions.json");

        return new ResponseEntity<>(json, headers, HttpStatus.OK);
    }
}

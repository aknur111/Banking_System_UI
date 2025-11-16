package com.example.bank.api.rest;

import com.example.bank.domain.transaction.model.Transaction;
import com.example.bank.domain.transaction.model.TransactionType;
import com.example.bank.domain.transaction.service.TransactionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody CreateTransactionRequest request
    ) {
        Transaction tx = transactionService.createTransaction(
                request.getAccountId(),
                request.getType(),
                request.getAmount(),
                request.getCurrency(),
                request.getDirection(),
                request.getDescription(),
                null
        );
        return ResponseEntity.ok(toResponse(tx));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TransactionResponse>> myTransactions() {
        Long demoAccountId = 1L;
        List<Transaction> list = transactionService.getAccountTransactions(demoAccountId);
        List<TransactionResponse> resp = list.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(
            @PathVariable Long accountId
    ) {
        List<Transaction> list = transactionService.getAccountTransactions(accountId);
        List<TransactionResponse> resp = list.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id) {
        Transaction tx = transactionService.getTransaction(id);
        return ResponseEntity.ok(toResponse(tx));
    }

    @PostMapping("/{id}/suspicious")
    public ResponseEntity<TransactionResponse> markAsSuspicious(
            @PathVariable Long id,
            @RequestBody MarkSuspiciousRequest request
    ) {
        Transaction tx = transactionService.markAsSuspicious(id, request.getReason());
        return ResponseEntity.ok(toResponse(tx));
    }


    @Data
    public static class CreateTransactionRequest {
        private Long accountId;
        private TransactionType type;
        private BigDecimal amount;
        private String currency;
        private String direction;
        private String description;
    }

    @Data
    public static class MarkSuspiciousRequest {
        private String reason;
    }

    @Data
    public static class TransactionResponse {
        private Long id;
        private Long accountId;
        private TransactionType type;
        private String status;
        private BigDecimal amount;
        private String currency;
        private String direction;
        private String description;
        private boolean suspicious;
        private String suspiciousReason;
        private OffsetDateTime createdAt;
    }

    private TransactionResponse toResponse(Transaction tx) {
        TransactionResponse r = new TransactionResponse();
        r.setId(tx.getId());
        r.setAccountId(tx.getAccount() != null ? tx.getAccount().getId() : null);
        r.setType(tx.getType());
        r.setStatus(tx.getStatus() != null ? tx.getStatus().name() : null);
        r.setAmount(tx.getAmount());
        r.setCurrency(tx.getCurrency());
        r.setDirection(tx.getDirection());
        r.setDescription(tx.getDescription());
        r.setSuspicious(tx.isSuspicious());
        r.setSuspiciousReason(tx.getSuspiciousReason());
        r.setCreatedAt(tx.getCreatedAt());
        return r;
    }
}

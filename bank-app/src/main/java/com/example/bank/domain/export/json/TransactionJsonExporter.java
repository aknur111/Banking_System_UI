package com.example.bank.domain.export.json;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.transaction.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionJsonExporter {

    private final ObjectMapper objectMapper;

    public String exportAccountTransactions(Account account, List<Transaction> transactions) {
        AccountTransactionsExport export = new AccountTransactionsExport();
        export.setAccountId(account.getId());
        export.setCurrency(account.getCurrency());
        export.setBalance(account.getBalance());
        export.setTransactions(
                transactions.stream()
                        .map(this::toDto)
                        .toList()
        );

        try {
            return objectMapper.writeValueAsString(export);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize transactions export to JSON", e);
        }
    }

    private TransactionExportDto toDto(Transaction tx) {
        TransactionExportDto dto = new TransactionExportDto();
        dto.setId(tx.getId());
        dto.setType(tx.getType().name());
        dto.setStatus(tx.getStatus().name());
        dto.setAmount(tx.getAmount());
        dto.setCurrency(tx.getCurrency());
        dto.setDirection(tx.getDirection());
        dto.setDescription(tx.getDescription());
        dto.setSuspicious(tx.isSuspicious());
        dto.setSuspiciousReason(tx.getSuspiciousReason());
        dto.setCreatedAt(tx.getCreatedAt());
        return dto;
    }

    @Data
    public static class AccountTransactionsExport {
        private Long accountId;
        private String currency;
        private BigDecimal balance;
        private List<TransactionExportDto> transactions;
    }

    @Data
    public static class TransactionExportDto {
        private Long id;
        private String type;
        private String status;
        private BigDecimal amount;
        private String currency;
        private String direction;
        private String description;
        private boolean suspicious;
        private String suspiciousReason;
        private OffsetDateTime createdAt;
    }
}

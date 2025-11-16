package com.example.bank.api.dto.payment;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long customerId;
    private Long fromAccountId;
    private Long fromCardId;
    private String category;
    private String providerName;
    private String details;
    private BigDecimal amount;
    private String currency;
    private String status;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime createdAt;
    private Long transactionId;
}

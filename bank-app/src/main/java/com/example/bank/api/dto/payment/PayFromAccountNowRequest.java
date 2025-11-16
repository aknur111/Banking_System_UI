package com.example.bank.api.dto.payment;

import com.example.bank.domain.payment.model.PaymentCategory;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayFromAccountNowRequest {
    private Long accountId;
    private BigDecimal amount;
    private String currency;
    private PaymentCategory category;
    private String providerName;
    private String detailsJson;
}

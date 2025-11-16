package com.example.bank.api.dto.account;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDepositRequest {
    private BigDecimal amount;
    private String currency;
    private String description;
}

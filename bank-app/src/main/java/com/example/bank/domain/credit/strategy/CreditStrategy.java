package com.example.bank.domain.credit.strategy;

import com.example.bank.domain.credit.model.CreditType;

import java.math.BigDecimal;

public interface CreditStrategy {
    BigDecimal getMinInterestRate();
    BigDecimal getMaxAmount();
    int getMaxTermMonths();
}
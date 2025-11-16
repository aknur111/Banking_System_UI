package com.example.bank.domain.credit.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BusinessCreditStrategy implements CreditStrategy {
    @Override
    public BigDecimal getMinInterestRate() {
        return BigDecimal.valueOf(7.0);
    }

    @Override
    public BigDecimal getMaxAmount() {
        return BigDecimal.valueOf(5000000);
    }

    @Override
    public int getMaxTermMonths() {
        return 120;
    }
}
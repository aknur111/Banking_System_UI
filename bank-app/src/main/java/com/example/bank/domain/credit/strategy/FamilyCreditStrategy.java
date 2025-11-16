package com.example.bank.domain.credit.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FamilyCreditStrategy implements CreditStrategy {
    @Override
    public BigDecimal getMinInterestRate() {
        return BigDecimal.valueOf(4.5);
    }

    @Override
    public BigDecimal getMaxAmount() {
        return BigDecimal.valueOf(2000000);
    }

    @Override
    public int getMaxTermMonths() {
        return 84;
    }
}
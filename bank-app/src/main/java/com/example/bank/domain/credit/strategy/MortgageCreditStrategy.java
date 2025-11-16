package com.example.bank.domain.credit.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MortgageCreditStrategy implements CreditStrategy {
    @Override
    public BigDecimal getMinInterestRate() {
        return BigDecimal.valueOf(3.5);
    }

    @Override
    public BigDecimal getMaxAmount() {
        return BigDecimal.valueOf(10000000);
    }

    @Override
    public int getMaxTermMonths() {
        return 360;
    }
}
package com.example.bank.domain.credit.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PersonalCreditStrategy implements CreditStrategy {
    @Override
    public BigDecimal getMinInterestRate() {
        return BigDecimal.valueOf(5.0);
    }

    @Override
    public BigDecimal getMaxAmount() {
        return BigDecimal.valueOf(1000000);
    }

    @Override
    public int getMaxTermMonths() {
        return 60;
    }
}
package com.example.bank.domain.credit.strategy;

import com.example.bank.domain.credit.model.CreditType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreditStrategyFactory {

    private final Map<CreditType, CreditStrategy> strategies;

    public CreditStrategyFactory(PersonalCreditStrategy personal,
                                 BusinessCreditStrategy business,
                                 FamilyCreditStrategy family,
                                 MortgageCreditStrategy mortgage,
                                 FarmerCreditStrategy farmer) {
        this.strategies = Map.of(
                CreditType.PERSONAL, personal,
                CreditType.BUSINESS, business,
                CreditType.FAMILY, family,
                CreditType.MORTGAGE, mortgage,
                CreditType.FARMER, farmer
        );
    }

    public CreditStrategy getStrategy(CreditType creditType) {
        CreditStrategy strategy = strategies.get(creditType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported credit type: " + creditType);
        }
        return strategy;
    }
}

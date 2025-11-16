package com.example.bank.domain.payment.bridge;

import com.example.bank.domain.payment.model.PaymentCategory;
import org.springframework.stereotype.Component;

@Component
public class UtilitiesPayment implements PaymentType{
    @Override
    public PaymentCategory getCategory() {
        return PaymentCategory.UTILITIES;
    }
}
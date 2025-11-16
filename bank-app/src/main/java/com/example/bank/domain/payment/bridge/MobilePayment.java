package com.example.bank.domain.payment.bridge;

import com.example.bank.domain.payment.model.Payment;
import com.example.bank.domain.payment.model.PaymentCategory;
import org.springframework.stereotype.Component;

@Component
public class MobilePayment implements PaymentType{
    @Override
    public PaymentCategory getCategory() {
        return PaymentCategory.MOBILE;
    }
}
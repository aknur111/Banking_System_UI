package com.example.bank.domain.payment.bridge;

import com.example.bank.domain.payment.model.PaymentCategory;

public interface PaymentType {

    PaymentCategory getCategory();

    default String buildDescription(String providerName, String detailsJson) {
        String base;

        switch (getCategory()) {
            case MOBILE -> base = "Mobile payment";
            case UTILITIES -> base = "Utilities payment";
            case EDUCATION -> base = "Education payment";
            default -> base = "Payment";
        }

        if (providerName != null && !providerName.isBlank()) {
            base += " to " + providerName;
        }

        if (detailsJson != null && !detailsJson.isBlank()) {
            base += " (" + detailsJson + ")";
        }

        return base;
    }
}

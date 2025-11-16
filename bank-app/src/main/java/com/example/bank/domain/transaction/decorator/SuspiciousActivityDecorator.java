package com.example.bank.domain.transaction.decorator;

import com.example.bank.domain.transaction.base.TransactionProcessor;
import com.example.bank.domain.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SuspiciousActivityDecorator implements TransactionProcessor {

    private final TransactionProcessor delegate;

    private final BigDecimal suspiciousAmountLimit;

    @Override
    public Transaction process(Transaction transaction) {
        if (transaction.getAmount() != null && suspiciousAmountLimit != null && transaction.getAmount().compareTo(suspiciousAmountLimit) > 0) {
            transaction.setSuspicious(true);

            if (transaction.getSuspiciousReason() == null) {
                transaction.setSuspiciousReason(
                        "Amount exceeds suspicious limit: " + suspiciousAmountLimit
                );
            }
        }

        return delegate.process(transaction);
    }
}

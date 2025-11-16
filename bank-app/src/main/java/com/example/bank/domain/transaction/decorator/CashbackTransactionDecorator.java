package com.example.bank.domain.transaction.decorator;

import com.example.bank.domain.transaction.base.TransactionProcessor;
import com.example.bank.domain.transaction.model.Transaction;
import com.example.bank.domain.transaction.model.TransactionType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CashbackTransactionDecorator implements TransactionProcessor {

    private final TransactionProcessor delegate;

    @Override
    public Transaction process(Transaction transaction) {
        if (transaction.getType() == TransactionType.CASHBACK) {
            String desc = transaction.getDescription();
            if (desc == null) {
                desc = "";
            }
            transaction.setDescription("Cashback transaction: " + desc);
        }
        return delegate.process(transaction);
    }
}

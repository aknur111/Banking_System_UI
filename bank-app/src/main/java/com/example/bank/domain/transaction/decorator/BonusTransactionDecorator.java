package com.example.bank.domain.transaction.decorator;

import com.example.bank.domain.transaction.base.TransactionProcessor;
import com.example.bank.domain.transaction.model.Transaction;
import com.example.bank.domain.transaction.model.TransactionType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BonusTransactionDecorator implements TransactionProcessor {

    private final TransactionProcessor delegate;

    @Override
    public Transaction process(Transaction transaction) {
        if (transaction.getType() == TransactionType.BONUS) {
            String desc = transaction.getDescription();
            if (desc == null) {
                desc = "";
            }
            transaction.setDescription("Bonus transaction: " + desc);
        }
        return delegate.process(transaction);
    }
}

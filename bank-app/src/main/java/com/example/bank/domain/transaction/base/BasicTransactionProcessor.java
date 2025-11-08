package com.example.bank.domain.transaction.base;

import com.example.bank.domain.transaction.model.Transaction;
import com.example.bank.domain.transaction.repository.TransactionRepository;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class BasicTransactionProcessor implements TransactionProcessor {
    private final TransactionRepository transactionRepository;

    @Override
    public Transaction process(Transaction transaction) {
        if(transaction.getCreatedAt() == null){
            transaction.setCreatedAt(OffsetDateTime.now());
        }

        return transactionRepository.save(transaction);
    }
}
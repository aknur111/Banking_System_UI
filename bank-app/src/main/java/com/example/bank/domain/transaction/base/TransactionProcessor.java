package com.example.bank.domain.transaction.base;

import com.example.bank.domain.transaction.model.Transaction;

public interface TransactionProcessor {
    Transaction process(Transaction transaction);
}
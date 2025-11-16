package com.example.bank.domain.payment.bridge;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.repository.AccountRepository;
import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.transaction.model.Transaction;
import com.example.bank.domain.transaction.model.TransactionType;
import com.example.bank.domain.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AccountPaymentChannel implements PaymentChannel {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    @Override
    @Transactional
    public Transaction pay(BigDecimal amount, String currency, String description, Account fromAccount, Card fromCard) {
        if (fromAccount == null) {
            throw new IllegalArgumentException("Source account must not be null");
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Not enough balance on account " + fromAccount.getId());
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountRepository.save(fromAccount);

        return transactionService.createTransaction(
                fromAccount.getId(),
                TransactionType.PAYMENT,
                amount,
                currency,
                "OUT",
                description,
                fromCard
        );
    }
}

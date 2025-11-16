package com.example.bank.domain.payment.bridge;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.repository.AccountRepository;
import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.card.repository.CardRepository;
import com.example.bank.domain.transaction.model.Transaction;
import com.example.bank.domain.transaction.model.TransactionType;
import com.example.bank.domain.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CardPaymentChannel implements PaymentChannel {

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final TransactionService transactionService;

    @Override
    @Transactional
    public Transaction pay(BigDecimal amount,
                           String currency,
                           String description,
                           Account fromAccount,
                           Card fromCard) {

        if (fromCard == null) {
            throw new IllegalArgumentException("CardPaymentChannel requires non-null card");
        }

        Card card = cardRepository.findById(fromCard.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Card not found: " + fromCard.getId()));

        Account account = card.getAccount();
        if (account == null) {
            throw new IllegalArgumentException("Card " + card.getId() + " has no linked account");
        }

        if (fromAccount != null && !account.getId().equals(fromAccount.getId())) {
            throw new IllegalArgumentException("Card does not belong to provided account");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Not enough balance on account " + account.getId());
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        return transactionService.createTransaction(
                account.getId(),
                TransactionType.PAYMENT,
                amount,
                currency,
                "OUT",
                description,
                card
        );
    }
}

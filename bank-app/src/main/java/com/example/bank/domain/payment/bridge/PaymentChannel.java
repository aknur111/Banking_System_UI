package com.example.bank.domain.payment.bridge;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.transaction.model.Transaction;
import java.math.BigDecimal;



public interface PaymentChannel {
    Transaction pay(BigDecimal amount, String currency, String description, Account fromAccount, Card fromCard);
}
package com.example.bank.domain.card.repository;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.card.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumber(String cardNumber);
    List<Card> findByAccount(Account account);
}
package com.example.bank.domain.card.service;

import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.card.model.CardType;

import java.util.List;
import java.util.Optional;

public interface CardService {
    List<Card> getAllCards();
    Card addCard(Card card);
    void removeCard(Long cardId);
    Optional<Card> getCard(Long cardId);
    CardType getType();
    Card createCard(Long accountId);
}

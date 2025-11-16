package com.example.bank.domain.card.service;

import com.example.bank.domain.card.factory.CreditCardFactory;
import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.card.model.CardType;
import com.example.bank.domain.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditCardService implements CardService {

    private final CardRepository cardRepository;
    private final CreditCardFactory creditCardFactory;

    @Override
    @Transactional(readOnly = true)
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @Override
    @Transactional
    public Card addCard(Card card) {
        return cardRepository.save(card);
    }

    @Override
    @Transactional
    public void removeCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Card> getCard(Long cardId) {
        return cardRepository.findById(cardId);
    }

    @Override
    public CardType getType() {
        return CardType.CREDIT;
    }

    @Override
    public Card createCard(Long accountId) {
        return creditCardFactory.createCard(accountId);
    }
}

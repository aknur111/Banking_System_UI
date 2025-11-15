package com.example.bank.domain.card.factory;

import com.example.bank.domain.card.model.Card;

public interface CardFactory {
    Card createCard(long accountId);
}
package com.example.bank.domain.notification.facade;

import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.notification.model.Notification;
import com.example.bank.domain.notification.model.NotificationType;
import com.example.bank.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardInfoFacade {
    private final NotificationFacade notificationFacade;
    public Notification notifyCardCreated(Card card){
        String cardNumber = card.getCardNumber();
        String masked = maskCardNumber(cardNumber);
        String title = "New card created";
        String message = "Your new card " + masked + " has been created";
        String payload = "{\"cardId\":" + card.getId() + "}";

        return notificationFacade.sendInApp(card.getAccount().getCustomer(), NotificationType.CARD, title, message, payload);
    }

    public Notification notifyCardBlocked(Card card){
        String cardNumber = card.getCardNumber();
        String masked = maskCardNumber(cardNumber);
        String title = "Card blocked";
        String message = "Your card " + masked + " has been blocked";
        String payload = "{\"cardId\":" + card.getId() + "}";
        return notificationFacade.sendInApp(card.getAccount().getCustomer(), NotificationType.CARD, title, message, payload);
    }
    private String maskCardNumber(String number){
        if(number == null || number.length() < 4){
            return "your card number";
        }
        return "**** **** ****"+ number.substring(number.length()-4);
    }
}
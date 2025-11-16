package com.example.bank.api.rest;

import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.card.model.CardType;
import com.example.bank.domain.card.service.CreditCardService;
import com.example.bank.domain.card.service.DebitCardService;
import com.example.bank.domain.notification.facade.CardInfoFacade;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final DebitCardService debitCardService;
    private final CreditCardService creditCardService;
    private final CardInfoFacade cardInfoFacade;

    @GetMapping("/my")
    public List<CardResponse> getMyCards() {

        return debitCardService.getAllCards().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCards() {
        List<Card> cards = debitCardService.getAllCards();
        List<CardResponse> responses = cards.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable Long id) {
        Optional<Card> cardOpt = debitCardService.getCard(id);
        if (cardOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Card card = cardOpt.get();
        card.updateStatusIfExpired();
        return ResponseEntity.ok(toResponse(card));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        debitCardService.removeCard(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/debit")
    public ResponseEntity<CardResponse> createDebitCard(@RequestBody CreateCardRequest request) {
        Card card = debitCardService.createCard(request.getAccountId());
        card = debitCardService.addCard(card);
        cardInfoFacade.notifyCardCreated(card);
        return ResponseEntity.status(201).body(toResponse(card));
    }

    @PostMapping("/credit")
    public ResponseEntity<CardResponse> createCreditCard(@RequestBody CreateCardRequest request) {
        Card card = creditCardService.createCard(request.getAccountId());
        card = creditCardService.addCard(card);
        cardInfoFacade.notifyCardCreated(card);
        return ResponseEntity.status(201).body(toResponse(card));
    }

    @GetMapping("/debit")
    public ResponseEntity<List<CardResponse>> getDebitCards() {
        List<CardResponse> responses = debitCardService.getAllCards().stream()
                .filter(c -> c.getType() == CardType.DEBIT)
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/credit")
    public ResponseEntity<List<CardResponse>> getCreditCards() {
        List<CardResponse> responses = debitCardService.getAllCards().stream()
                .filter(c -> c.getType() == CardType.CREDIT)
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }


    private CardResponse toResponse(Card card) {
        CardResponse r = new CardResponse();
        r.setId(card.getId());
        r.setCardNumber(card.getCardNumber());
        if (card.getAccount() != null) {
            r.setAccountId(card.getAccount().getId());
            if (card.getAccount().getCustomer() != null) {
                r.setHolderName(card.getAccount().getCustomer().getFullName());
            }
        }
        r.setStatus(card.getStatus() != null ? card.getStatus().name() : null);
        r.setType(card.getType() != null ? card.getType().name() : null);
        r.setExpiryMonth(card.getExpiryMonth());
        r.setExpiryYear(card.getExpiryYear());



        return r;
    }

    @Data
    public static class CreateCardRequest {
        private Long accountId;
    }

    @Data
    public static class CardResponse {
        private Long id;
        private String cardNumber;
        private Long accountId;
        private String holderName;
        private String status;
        private String type;
        private Integer expiryMonth;
        private Integer expiryYear;
        private BigDecimal dailyLimit;
        private String currency;
    }
}

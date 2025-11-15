package com.example.bank.api.rest;

import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.card.model.CardStatus;
import com.example.bank.domain.card.model.CardType;
import com.example.bank.domain.card.service.CreditCardService;
import com.example.bank.domain.card.service.DebitCardService;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CardController {

    private final DebitCardService debitCardService;
    private final CreditCardService creditCardService;


    @GetMapping("/api/cards")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<List<CardResponse>> getAllCards() {
        List<Card> cards = debitCardService.getAllCards();
        List<CardResponse> responses = cards.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/cards/{id}")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<CardResponse> getCard(@PathVariable Long id) {
        Optional<Card> cardOpt = debitCardService.getCard(id);
        if (cardOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Card card = cardOpt.get();
        card.updateStatusIfExpired();
        return ResponseEntity.ok(toResponse(card));
    }

    @DeleteMapping("/api/cards/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        debitCardService.removeCard(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/api/cards/debit")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<CardResponse> createDebitCard(@RequestBody CreateCardRequest request) {
        Card card = debitCardService.createCard(request.getAccountId());
        card = debitCardService.addCard(card);
        return ResponseEntity.status(201).body(toResponse(card));
    }

    @PostMapping("/api/cards/credit")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<CardResponse> createCreditCard(@RequestBody CreateCardRequest request) {
        Card card = creditCardService.createCard(request.getAccountId());
        card = creditCardService.addCard(card);
        return ResponseEntity.status(201).body(toResponse(card));
    }

    @GetMapping("/api/cards/debit")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<List<CardResponse>> getDebitCards() {
        List<CardResponse> responses = debitCardService.getAllCards().stream()
                .filter(c -> c.getType() == CardType.DEBIT)
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/cards/credit")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<List<CardResponse>> getCreditCards() {
        List<CardResponse> responses = debitCardService.getAllCards().stream()
                .filter(c -> c.getType() == CardType.CREDIT)
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/ui/cards")
    @PreAuthorize("hasRole('USER')")
    public String getCardsPage(Model model) {
        List<CardDto> cardDtos = debitCardService.getAllCards().stream()
                .map(this::toDto)
                .toList();
        model.addAttribute("cards", cardDtos);
        return "cards";
    }

    @PostMapping("/ui/cards/debit")
    @PreAuthorize("hasRole('USER')")
    public String createDebitCardUi(@RequestParam Long accountId) {
        Card card = debitCardService.createCard(accountId);
        debitCardService.addCard(card);
        return "redirect:/ui/cards";
    }

    @PostMapping("/ui/cards/credit")
    @PreAuthorize("hasRole('USER')")
    public String createCreditCardUi(@RequestParam Long accountId) {
        Card card = creditCardService.createCard(accountId);
        creditCardService.addCard(card);
        return "redirect:/ui/cards";
    }


    private CardResponse toResponse(Card card) {
        CardResponse r = new CardResponse();
        r.setId(card.getId());
        r.setMaskedCardNumber(mask(card.getCardNumber()));
        r.setExpiryMonth(card.getExpiryMonth());
        r.setExpiryYear(card.getExpiryYear());
        r.setStatus(card.getStatus().name());
        r.setType(card.getType().name());
        return r;
    }

    private CardDto toDto(Card card) {
        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setMaskedCardNumber(mask(card.getCardNumber()));
        dto.setAccountId(card.getAccount().getId());
        dto.setStatus(card.getStatus().name());
        dto.setType(card.getType().name());
        dto.setExpiryMonth(card.getExpiryMonth());
        dto.setExpiryYear(card.getExpiryYear());
        return dto;
    }

    private static String mask(String pan) {
        if (pan == null) return null;
        int len = pan.length();
        if (len <= 4) return pan;
        String last4 = pan.substring(len - 4);
        return "************" + last4;
    }


    @Data
    public static class CreateCardRequest {
        private Long accountId;
    }

    @Data
    public static class CardResponse {
        private Long id;
        private String maskedCardNumber;
        private int expiryMonth;
        private int expiryYear;
        private String status;
        private String type;
    }

    @Setter
    @Getter
    public static class CardDto {
        private Long id;
        private String maskedCardNumber;
        private Long accountId;
        private String status;
        private String type;
        private int expiryMonth;
        private int expiryYear;
    }
}

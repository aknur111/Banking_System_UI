package com.example.bank.domain.card.model;

import com.example.bank.domain.account.model.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "card_number", nullable = false, unique = true, length = 16)
    private String cardNumber;

    @Column(name = "expiry_month", nullable = false)
    private int expiryMonth;

    @Column(name = "expiry_year", nullable = false)
    private int expiryYear;

    @Column(nullable = false, length = 3)
    private String cvv;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardType type;   // 👉 новое поле

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (status == null) {
            status = CardStatus.ACTIVE;
        }
        if (type == null) {
            type = CardType.DEBIT;
        }
    }

    public void updateStatusIfExpired() {
        YearMonth exp = YearMonth.of(expiryYear, expiryMonth);
        YearMonth now = YearMonth.now();

        if (status != CardStatus.BLOCKED && status != CardStatus.INACTIVE) {
            if (now.isAfter(exp)) {
                this.status = CardStatus.EXPIRED;
            }
        }
    }
}

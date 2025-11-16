package com.example.bank.domain.credit.model;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.currency.model.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "credits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private CustomerProfile customer;

    @Column(name = "principal_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(name = "interest_rate_annual", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRateAnnual;

    @Column(name = "term_months", nullable = false)
    private int termMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_type", nullable = false, length = 50)
    private CreditType creditType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CreditStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;
}
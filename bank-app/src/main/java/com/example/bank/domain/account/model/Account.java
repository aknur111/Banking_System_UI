package com.example.bank.domain.account.model;

import com.example.bank.domain.customer.model.CustomerProfile;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private CustomerProfile customer;

    @Column(nullable = false, length = 3)
    private String currency;


    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) createdAt = OffsetDateTime.now();
        if(balance == null) balance = BigDecimal.ZERO;
    }
}
package com.example.bank.domain.credit.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "installment_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "credit_id")
    private Credit credit;

    @Column(name = "installment_no", nullable = false)
    private int installmentNo;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private boolean paid;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "penalty_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal penaltyAmount;
}
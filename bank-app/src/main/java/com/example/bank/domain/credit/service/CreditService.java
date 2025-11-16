package com.example.bank.domain.credit.service;

import com.example.bank.domain.credit.model.Credit;
import com.example.bank.domain.credit.model.CreditStatus;
import com.example.bank.domain.credit.model.CreditType;
import com.example.bank.domain.credit.repository.CreditRepository;
import com.example.bank.domain.currency.model.Currency;
import com.example.bank.domain.customer.model.CustomerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;

    @Transactional
    public Credit createCredit(CustomerProfile customer,
                               BigDecimal principalAmount,
                               Currency currency,
                               BigDecimal interestRateAnnual,
                               int termMonths,
                               CreditType creditType) {

        if (customer == null) {
            throw new IllegalArgumentException("Customer is required");
        }
        if (principalAmount == null || principalAmount.signum() <= 0) {
            throw new IllegalArgumentException("Principal amount must be positive");
        }
        if (interestRateAnnual == null || interestRateAnnual.signum() <= 0) {
            throw new IllegalArgumentException("Interest rate must be positive");
        }
        if (termMonths <= 0) {
            throw new IllegalArgumentException("Term months must be > 0");
        }

        Credit credit = Credit.builder()
                .customer(customer)
                .principalAmount(principalAmount)
                .currency(currency)
                .interestRateAnnual(interestRateAnnual)
                .termMonths(termMonths)
                .creditType(creditType)
                .status(CreditStatus.ACTIVE)
                .createdAt(OffsetDateTime.now())
                .build();

        return creditRepository.save(credit);
    }

    @Transactional(readOnly = true)
    public List<Credit> getCreditsByCustomer(CustomerProfile customer) {
        return creditRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    @Transactional(readOnly = true)
    public Optional<Credit> getCredit(Long id) {
        return creditRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Credit> getAllCredits() {
        return creditRepository.findAll();
    }

    @Transactional
    public Credit closeCredit(Long id) {
        Credit credit = creditRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Credit not found: " + id));

        if (credit.getStatus() == CreditStatus.CLOSED) {
            return credit;
        }

        credit.setStatus(CreditStatus.CLOSED);
        credit.setClosedAt(OffsetDateTime.now());
        return creditRepository.save(credit);
    }
}

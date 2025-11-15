package com.example.bank.domain.payment.service;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.repository.AccountRepository;
import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.card.model.CardStatus;
import com.example.bank.domain.card.repository.CardRepository;
import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.payment.bridge.AccountPaymentChannel;
import com.example.bank.domain.payment.bridge.CardPaymentChannel;
import com.example.bank.domain.payment.bridge.PaymentType;
import com.example.bank.domain.payment.model.Payment;
import com.example.bank.domain.payment.model.PaymentCategory;
import com.example.bank.domain.payment.model.PaymentStatus;
import com.example.bank.domain.payment.repository.PaymentRepository;
import com.example.bank.domain.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final AccountPaymentChannel accountPaymentChannel;
    private final CardPaymentChannel cardPaymentChannel;
    private final List<PaymentType> paymentTypes;

    @Transactional
    public Payment payFromAccountNow(Long accountId,
                                     BigDecimal amount,
                                     String currency,
                                     PaymentCategory category,
                                     String providerName,
                                     String detailsJson) {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found " + accountId));

        PaymentType paymentType = resolveType(category);
        String description = paymentType.buildDescription(providerName, detailsJson);

        Payment payment = Payment.builder()
                .customer(account.getCustomer())
                .fromAccount(account)
                .category(category)
                .providerName(providerName)
                .details(detailsJson)
                .amount(amount)
                .currency(currency)
                .status(PaymentStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        try {
            Transaction tx = accountPaymentChannel.pay(
                    amount,
                    currency,
                    description,
                    account,
                    null
            );

            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(OffsetDateTime.now());
            payment.setTransaction(tx);
        } catch (Exception ex) {
            payment.setStatus(PaymentStatus.FAILED);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment payFromCardNow(Long cardId,
                                  BigDecimal amount,
                                  String currency,
                                  PaymentCategory category,
                                  String providerName,
                                  String detailsJson) {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        // обновим статус, если карта истекла (метод мы добавляли в Card)
        card.updateStatusIfExpired();
        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new IllegalStateException("Card is expired");
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Card is not active: " + card.getStatus());
        }

        Account account = card.getAccount();
        if (account == null) {
            throw new IllegalStateException("Card " + cardId + " has no linked account");
        }

        PaymentType paymentType = resolveType(category);
        String description = paymentType.buildDescription(providerName, detailsJson);

        Payment payment = Payment.builder()
                .customer(account.getCustomer())
                .fromCard(card)
                .category(category)
                .providerName(providerName)
                .details(detailsJson)
                .amount(amount)
                .currency(currency)
                .status(PaymentStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        try {
            Transaction tx = cardPaymentChannel.pay(
                    amount,
                    currency,
                    description,
                    null,
                    card
            );

            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(OffsetDateTime.now());
            payment.setTransaction(tx);
        } catch (Exception ex) {
            payment.setStatus(PaymentStatus.FAILED);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment scheduleFromAccount(Long accountId,
                                       BigDecimal amount,
                                       String currency,
                                       PaymentCategory category,
                                       String providerName,
                                       String detailsJson,
                                       OffsetDateTime when) {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        Payment payment = Payment.builder()
                .customer(account.getCustomer())
                .fromAccount(account)
                .category(category)
                .providerName(providerName)
                .details(detailsJson)
                .amount(amount)
                .currency(currency)
                .status(PaymentStatus.SCHEDULED)
                .scheduledAt(when)
                .createdAt(OffsetDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment scheduleFromCard(Long cardId,
                                    BigDecimal amount,
                                    String currency,
                                    PaymentCategory category,
                                    String providerName,
                                    String detailsJson,
                                    OffsetDateTime when) {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        Account account = card.getAccount();
        if (account == null) {
            throw new IllegalStateException("Card " + cardId + " has no linked account");
        }

        Payment payment = Payment.builder()
                .customer(account.getCustomer())
                .fromCard(card)
                .category(category)
                .providerName(providerName)
                .details(detailsJson)
                .amount(amount)
                .currency(currency)
                .status(PaymentStatus.SCHEDULED)
                .scheduledAt(when)
                .createdAt(OffsetDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }


    @Transactional
    public Payment executeScheduledPayment(Long paymentId) {
        Payment payment = getPayment(paymentId);

        if (payment.getStatus() != PaymentStatus.SCHEDULED) {
            throw new IllegalStateException("Payment is not scheduled: " + paymentId);
        }

        PaymentCategory category = payment.getCategory();
        PaymentType type = resolveType(category);
        String description = type.buildDescription(payment.getProviderName(), payment.getDetails());

        try {
            Transaction tx;

            if (payment.getFromAccount() != null) {
                tx = accountPaymentChannel.pay(
                        payment.getAmount(),
                        payment.getCurrency(),
                        description,
                        payment.getFromAccount(),
                        null
                );
            } else if (payment.getFromCard() != null) {

                Card card = payment.getFromCard();
                card.updateStatusIfExpired();
                if (card.getStatus() == CardStatus.EXPIRED) {
                    throw new IllegalStateException("Card is expired");
                }
                if (card.getStatus() != CardStatus.ACTIVE) {
                    throw new IllegalStateException("Card is not active: " + card.getStatus());
                }

                tx = cardPaymentChannel.pay(
                        payment.getAmount(),
                        payment.getCurrency(),
                        description,
                        null,
                        card
                );
            } else {
                throw new IllegalStateException("Scheduled payment has no source (account/card)");
            }

            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(OffsetDateTime.now());
            payment.setTransaction(tx);
        } catch (Exception ex) {
            payment.setStatus(PaymentStatus.FAILED);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment cancelScheduledPayment(Long paymentId) {
        Payment payment = getPayment(paymentId);

        if (payment.getStatus() != PaymentStatus.SCHEDULED) {
            throw new IllegalStateException("Payment is not scheduled: " + paymentId);
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public Payment getPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Payment> getCustomerPayments(CustomerProfile customer) {
        return paymentRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    @Transactional(readOnly = true)
    public List<Payment> getCustomerPaymentsByStatus(CustomerProfile customer, PaymentStatus status) {
        return paymentRepository.findByCustomerAndStatus(customer, status);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByCategory(PaymentCategory category) {
        return paymentRepository.findByCategory(category);
    }

    private PaymentType resolveType(PaymentCategory category) {
        return paymentTypes.stream()
                .filter(t -> t.getCategory() == category)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported payment category: " + category
                ));
    }
}

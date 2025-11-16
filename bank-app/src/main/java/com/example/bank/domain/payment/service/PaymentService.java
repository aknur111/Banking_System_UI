package com.example.bank.domain.payment.service;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.repository.AccountRepository;
import com.example.bank.domain.bonus.service.BonusService;
import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.card.model.CardStatus;
import com.example.bank.domain.card.repository.CardRepository;
import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.notification.facade.ReceiptFacade;
import com.example.bank.domain.notification.model.NotificationType;
import com.example.bank.domain.notification.service.NotificationService;
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
    private final BonusService bonusService;
    private final ReceiptFacade receiptFacade;
    private final NotificationService notificationService;

    @Transactional
    public Payment createPayment(
            String sourceType,
            Long sourceId,
            BigDecimal amount,
            String currency,
            PaymentCategory category,
            String providerName,
            String detailsJson
    ) {
        if (sourceType == null || sourceId == null) {
            throw new IllegalArgumentException("sourceType и sourceId обязательны");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        SourceType type;
        try {
            type = SourceType.valueOf(sourceType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported sourceType: " + sourceType);
        }

        return switch (type) {
            case ACCOUNT -> payFromAccountNow(sourceId, amount, currency, category, providerName, detailsJson);
            case CARD -> payFromCardNow(sourceId, amount, currency, category, providerName, detailsJson);
        };
    }

    @Transactional(readOnly = true)
    public List<Payment> getAccountPayments(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found " + accountId));

        return paymentRepository.findByFromAccountOrderByCreatedAtDesc(account);
    }


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
        CustomerProfile customer = account.getCustomer();

        String description = buildDescription(category, providerName, detailsJson);

        Payment payment = Payment.builder()
                .customer(customer)
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

            payment = paymentRepository.save(payment);

            receiptFacade.sendPaymentReceipt(payment);

        } catch (Exception ex) {
            payment.setStatus(PaymentStatus.FAILED);
            payment = paymentRepository.save(payment);

            notificationService.notifyInApp(
                    customer,
                    NotificationType.PAYMENT,
                    "Payment from account failed",
                    "Payment to provider " + providerName + " failed: " + ex.getMessage(),
                    "{\"paymentId\":" + payment.getId() + "}"
            );
        }

        return payment;
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
        CustomerProfile customer = account.getCustomer();

        String description = buildDescription(category, providerName, detailsJson);

        Payment payment = Payment.builder()
                .customer(customer)
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

            payment = paymentRepository.save(payment);

            if (category != null) {
                bonusService.applyCashback(
                        account,
                        amount,
                        category.name()
                );
            }

            receiptFacade.sendPaymentReceipt(payment);

        } catch (Exception ex) {
            payment.setStatus(PaymentStatus.FAILED);
            payment = paymentRepository.save(payment);

            notificationService.notifyInApp(
                    customer,
                    NotificationType.PAYMENT,
                    "Payment from card failed",
                    "Payment to provider " + providerName + " failed: " + ex.getMessage(),
                    "{\"paymentId\":" + payment.getId() + "}"
            );
        }

        return payment;
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


    private String buildDescription(PaymentCategory category, String providerName, String detailsJson) {
        if (category == null) {
            return providerName != null ? providerName : "Payment";
        }

        PaymentType paymentType = resolveType(category);
        return paymentType.buildDescription(providerName, detailsJson);
    }

    private PaymentType resolveType(PaymentCategory category) {
        return paymentTypes.stream()
                .filter(t -> t.getCategory() == category)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported payment category: " + category
                ));
    }

    private enum SourceType {
        ACCOUNT,
        CARD
    }
}

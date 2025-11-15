package com.example.bank.api.rest;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.payment.model.Payment;
import com.example.bank.domain.payment.model.PaymentCategory;
import com.example.bank.domain.payment.model.PaymentStatus;
import com.example.bank.domain.payment.service.PaymentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/account/now")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> payFromAccountNow(
            @RequestBody PayFromAccountRequest request
    ) {
        Payment payment = paymentService.payFromAccountNow(
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency(),
                request.getCategory(),
                request.getProviderName(),
                request.getDetails()
        );
        return ResponseEntity.status(201).body(toResponse(payment));
    }


    @PostMapping("/card/now")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> payFromCardNow(
            @RequestBody PayFromCardRequest request
    ) {
        Payment payment = paymentService.payFromCardNow(
                request.getCardId(),
                request.getAmount(),
                request.getCurrency(),
                request.getCategory(),
                request.getProviderName(),
                request.getDetails()
        );
        return ResponseEntity.status(201).body(toResponse(payment));
    }


    @PostMapping("/account/schedule")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> scheduleFromAccount(
            @RequestBody ScheduleFromAccountRequest request
    ) {
        Payment payment = paymentService.scheduleFromAccount(
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency(),
                request.getCategory(),
                request.getProviderName(),
                request.getDetails(),
                request.getScheduledAt()
        );
        return ResponseEntity.status(201).body(toResponse(payment));
    }


    @PostMapping("/card/schedule")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> scheduleFromCard(
            @RequestBody ScheduleFromCardRequest request
    ) {
        Payment payment = paymentService.scheduleFromCard(
                request.getCardId(),
                request.getAmount(),
                request.getCurrency(),
                request.getCategory(),
                request.getProviderName(),
                request.getDetails(),
                request.getScheduledAt()
        );
        return ResponseEntity.status(201).body(toResponse(payment));
    }


    @PostMapping("/{id}/execute")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> executeScheduled(@PathVariable Long id) {
        Payment payment = paymentService.executeScheduledPayment(id);
        return ResponseEntity.ok(toResponse(payment));
    }


    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> cancelScheduled(@PathVariable Long id) {
        Payment payment = paymentService.cancelScheduledPayment(id);
        return ResponseEntity.ok(toResponse(payment));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        Payment payment = paymentService.getPayment(id);
        return ResponseEntity.ok(toResponse(payment));
    }


    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<PaymentResponse>> getCustomerPayments(
            @AuthenticationPrincipal CustomerProfile customer,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) PaymentCategory category
    ) {
        List<Payment> payments;

        if (status != null) {
            payments = paymentService.getCustomerPaymentsByStatus(customer, status);
        } else {
            payments = paymentService.getCustomerPayments(customer);
        }

        if (category != null) {
            payments = payments.stream()
                    .filter(p -> p.getCategory() == category)
                    .toList();
        }

        List<PaymentResponse> responses = payments.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }


    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse r = new PaymentResponse();
        r.setId(payment.getId());
        r.setAmount(payment.getAmount());
        r.setCurrency(payment.getCurrency());
        r.setCategory(payment.getCategory().name());
        r.setStatus(payment.getStatus().name());
        r.setProviderName(payment.getProviderName());
        r.setDetails(payment.getDetails());
        r.setCreatedAt(payment.getCreatedAt());
        r.setPaidAt(payment.getPaidAt());
        r.setScheduledAt(payment.getScheduledAt());

        if (payment.getFromAccount() != null) {
            r.setSourceType("ACCOUNT");
            r.setSourceAccountId(payment.getFromAccount().getId());
        } else if (payment.getFromCard() != null) {
            r.setSourceType("CARD");
            r.setSourceCardId(payment.getFromCard().getId());
        } else {
            r.setSourceType("UNKNOWN");
        }

        if (payment.getTransaction() != null) {
            r.setTransactionId(payment.getTransaction().getId());
        }

        return r;
    }


    @Data
    public static class PayFromAccountRequest {
        private Long accountId;
        private BigDecimal amount;
        private String currency;
        private PaymentCategory category;
        private String providerName;
        private String details;
    }

    @Data
    public static class PayFromCardRequest {
        private Long cardId;
        private BigDecimal amount;
        private String currency;
        private PaymentCategory category;
        private String providerName;
        private String details;
    }

    @Data
    public static class ScheduleFromAccountRequest {
        private Long accountId;
        private BigDecimal amount;
        private String currency;
        private PaymentCategory category;
        private String providerName;
        private String details;
        private OffsetDateTime scheduledAt;
    }

    @Data
    public static class ScheduleFromCardRequest {
        private Long cardId;
        private BigDecimal amount;
        private String currency;
        private PaymentCategory category;
        private String providerName;
        private String details;
        private OffsetDateTime scheduledAt;
    }

    @Data
    public static class PaymentResponse {
        private Long id;
        private BigDecimal amount;
        private String currency;
        private String category;
        private String status;
        private String providerName;
        private String details;

        private String sourceType;
        private Long sourceAccountId;
        private Long sourceCardId;

        private Long transactionId;

        private OffsetDateTime createdAt;
        private OffsetDateTime paidAt;
        private OffsetDateTime scheduledAt;
    }
}

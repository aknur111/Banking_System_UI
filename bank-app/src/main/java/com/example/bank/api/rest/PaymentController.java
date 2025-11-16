package com.example.bank.api.rest;

import com.example.bank.domain.payment.model.Payment;
import com.example.bank.domain.payment.model.PaymentCategory;
import com.example.bank.domain.payment.service.PaymentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments() {
        Long demoAccountId = 1L;

        List<Payment> payments = paymentService.getAccountPayments(demoAccountId);

        List<PaymentResponse> responses = payments.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }


    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) {

        PaymentCategory category = null;
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            category = PaymentCategory.valueOf(request.getCategory().trim().toUpperCase());
        }

        Payment payment = paymentService.createPayment(
                request.getSourceType(),
                request.getSourceId(),
                request.getAmount(),
                request.getCurrency(),
                category,
                request.getProviderName(),
                request.getDetails()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(payment));
    }


    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        Payment payment = paymentService.getPayment(id);
        return ResponseEntity.ok(toResponse(payment));
    }


    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse r = new PaymentResponse();
        r.setId(payment.getId());
        r.setAmount(payment.getAmount());
        r.setCurrency(payment.getCurrency());
        r.setCategory(payment.getCategory() != null ? payment.getCategory().name() : null);
        r.setStatus(payment.getStatus() != null ? payment.getStatus().name() : null);
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
    public static class CreatePaymentRequest {
        private String sourceType;
        private Long sourceId;
        private BigDecimal amount;
        private String currency;
        private String category;
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

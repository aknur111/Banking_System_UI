package com.example.bank.domain.notification.facade;

import com.example.bank.domain.deposit.model.Deposit;
import com.example.bank.domain.notification.model.Notification;
import com.example.bank.domain.notification.model.NotificationType;
import com.example.bank.domain.payment.model.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReceiptFacade {

    private final NotificationFacade notificationFacade;
    private final ObjectMapper objectMapper;

    public Notification sendPaymentReceipt(Payment payment) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentId", payment.getId());
        payload.put("amount", payment.getAmount());
        payload.put("currency", payment.getCurrency());
        payload.put("status", payment.getStatus());
        payload.put("category", payment.getCategory());

        String payloadJson = toJson(payload);
        String title = "Payment receipt";
        String message = "Payment " + payment.getId()
                + " for " + payment.getAmount() + " " + payment.getCurrency()
                + " has status " + payment.getStatus();

        return notificationFacade.sendInApp(
                payment.getCustomer(),
                NotificationType.PAYMENT,
                title,
                message,
                payloadJson
        );
    }

    public Notification sendDepositOpenedReceipt(Deposit deposit, String emailTo) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("depositId", deposit.getId());
        payload.put("accountId", deposit.getAccount().getId());
        payload.put("amount", deposit.getPrincipalAmount());
        payload.put("currency", deposit.getCurrency());
        payload.put("monthlyInterest", deposit.getMonthlyInterest());
        payload.put("termMonths", deposit.getTermMonths());
        payload.put("status", deposit.getStatus());
        payload.put("openedAt", deposit.getOpenedAt());

        String payloadJson = toJson(payload);
        String title = "Deposit opened";
        String message = "Deposit " + deposit.getId()
                + " for " + deposit.getPrincipalAmount() + " " + deposit.getCurrency()
                + " has been opened";

        if (emailTo != null && !emailTo.isBlank()) {
            return notificationFacade.sendEmailAndApp(
                    deposit.getCustomer(),
                    NotificationType.DEPOSIT,
                    title,
                    message,
                    payloadJson,
                    emailTo
            );
        } else {
            return notificationFacade.sendInApp(
                    deposit.getCustomer(),
                    NotificationType.DEPOSIT,
                    title,
                    message,
                    payloadJson
            );
        }
    }

    public Notification sendDepositClosedReceipt(Deposit deposit, String emailTo) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("depositId", deposit.getId());
        payload.put("accountId", deposit.getAccount().getId());
        payload.put("amount", deposit.getPrincipalAmount());
        payload.put("currency", deposit.getCurrency());
        payload.put("monthlyInterest", deposit.getMonthlyInterest());
        payload.put("termMonths", deposit.getTermMonths());
        payload.put("status", deposit.getStatus());
        payload.put("openedAt", deposit.getOpenedAt());
        payload.put("closedAt", deposit.getClosedAt());

        String payloadJson = toJson(payload);
        String title = "Deposit closed";
        String message = "Deposit " + deposit.getId()
                + " has been closed. Status: " + deposit.getStatus();

        if (emailTo != null && !emailTo.isBlank()) {
            return notificationFacade.sendEmailAndApp(
                    deposit.getCustomer(),
                    NotificationType.DEPOSIT,
                    title,
                    message,
                    payloadJson,
                    emailTo
            );
        } else {
            return notificationFacade.sendInApp(
                    deposit.getCustomer(),
                    NotificationType.DEPOSIT,
                    title,
                    message,
                    payloadJson
            );
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}

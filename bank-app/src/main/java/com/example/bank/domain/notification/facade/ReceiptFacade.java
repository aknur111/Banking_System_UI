package com.example.bank.domain.notification.facade;

import com.example.bank.domain.notification.model.Notification;
import com.example.bank.domain.notification.model.NotificationType;
import com.example.bank.domain.notification.service.NotificationService;
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

    public Notification sendPaymentReceipt(Payment payment){
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentId", payment.getId());
        payload.put("amount", payment.getAmount());
        payload.put("currency", payment.getCurrency());
        payload.put("status", payment.getStatus());
        payload.put("category", payment.getCategory());

        String payloadJson = toJson(payload);
        String title = "Payment receipt";
        String message = "Payment " + payment.getId()+ " for "+payment.getAmount()+" "+payment.getCurrency() + " has status "+payment.getStatus();
        return notificationFacade.sendInApp(payment.getCustomer(), NotificationType.PAYMENT, title, message, payloadJson);

    }
    private String toJson(Object value){
        try{
            return objectMapper.writeValueAsString(value);
        }catch(JsonProcessingException e){
            return "{}";
        }
    }
}
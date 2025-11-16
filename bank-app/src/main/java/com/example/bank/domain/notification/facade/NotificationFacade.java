package com.example.bank.domain.notification.facade;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.notification.model.Notification;
import com.example.bank.domain.notification.model.NotificationType;
import com.example.bank.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationFacade {
    private final NotificationService notificationService;
    public Notification sendInApp(CustomerProfile customer, NotificationType type, String title,String message, String payloadJson) {
        return notificationService.notifyInApp(customer, type, title, message, payloadJson);
    }

    public Notification sendEmailAndApp(CustomerProfile customer, NotificationType type, String title, String message, String payloadJson, String emailTo) {
        return notificationService.notifyCustomer(customer, type, "EMAIL", title, message, payloadJson, emailTo, true, true);
    }
}
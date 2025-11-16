package com.example.bank.domain.notification.service;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.notification.email.EmailNotifier;
import com.example.bank.domain.notification.model.Notification;
import com.example.bank.domain.notification.model.NotificationType;
import com.example.bank.domain.notification.repository.NotificationRepository;
import com.example.bank.domain.notification.websocket.WebSocketNotifier;
import com.example.bank.infrastructure.logging.AuditLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailNotifier emailNotifier;
    private final WebSocketNotifier webSocketNotifier;
    private final AuditLogger auditLogger;

    public Notification notifyCustomer(CustomerProfile customer, NotificationType type, String channel, String title, String message, String payloadJson, String emailTo, boolean sendEmail, boolean sendWebSocket) {
        Notification notification = Notification.builder()
                .customer(customer)
                .type(type.name())
                .channel(channel)
                .title(title)
                .message(message)
                .payload(payloadJson)
                .read(false)
                .createdAt(OffsetDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        auditLogger.log("NOTIFICATION_CREATED", "id=" + saved.getId()+", type="+type+", customer="+customer.getId());
        if(sendEmail && emailTo != null && !emailTo.isBlank()) {
            emailNotifier.sendNotificationEmail(emailTo, saved);
        }
        if(sendWebSocket) {
            webSocketNotifier.sendNotification(saved);
        }
        return saved;
    }

    public Notification notifyInApp(CustomerProfile customer, NotificationType type, String title, String message, String payloadJson) {
        return notifyCustomer(customer, type, "IN_APP", title, message, payloadJson, null, false, true);
    }

    @Transactional(readOnly = true)
    public List<Notification> getCustomerNotifications(CustomerProfile customer) {
        return notificationRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(CustomerProfile customer) {
        return notificationRepository.findByCustomerAndReadIsFalse(customer);
    }
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new IllegalArgumentException("Notification not found" + notificationId));
    if(!notification.isRead()) {
        notification.setRead(true);
        notification = notificationRepository.save(notification);
    }
    return notification;
    }

    public void markAllAsRead(CustomerProfile customer) {
        List<Notification> notifications = notificationRepository.findByCustomerAndReadIsFalse(customer);

        for (Notification n : notifications) {
            n.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }
    public Notification createNotification(
            CustomerProfile customer,
            String type,
            String channel,
            String title,
            String message,
            String payloadJson
    ) {
        Notification notification = Notification.builder()
                .customer(customer)
                .type(type)
                .channel(channel)
                .title(title)
                .message(message)
                .payload(payloadJson)
                .read(false)
                .createdAt(OffsetDateTime.now())
                .build();

        return notificationRepository.save(notification);
    }
}
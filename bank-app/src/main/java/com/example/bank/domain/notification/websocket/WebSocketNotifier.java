package com.example.bank.domain.notification.websocket;

import com.example.bank.domain.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketNotifier {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendNotification(Notification notification) {
        long customerId = notification.getCustomer().getId();
        simpMessagingTemplate.convertAndSend(
                "/topic/notifications/" + customerId,
                notification
        );
    }
}

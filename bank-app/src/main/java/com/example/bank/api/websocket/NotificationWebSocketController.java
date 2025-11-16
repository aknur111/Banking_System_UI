package com.example.bank.api.websocket;

import com.example.bank.domain.notification.model.Notification;
import com.example.bank.domain.notification.service.NotificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {

    private final NotificationService notificationService;

    @MessageMapping("/notifications.markRead")
    @SendToUser("/queue/notifications")
    public Notification markRead(@Payload MarkReadMessage message) {
        return notificationService.markAsRead(message.getNotificationId());
    }

    @Data
    public static class MarkReadMessage {
        private Long notificationId;
    }
}

package com.example.bank.domain.notification.email;

import com.example.bank.domain.notification.model.Notification;
import com.example.bank.infrastructure.email.SmtpMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


@Component
@RequiredArgsConstructor
public class EmailNotifier {

    private final SmtpMailSender smtpMailSender;

    @Value("${app.mail.from:noreply@bank.local}")
    private String defaultFrom;

    public void sendNotificationEmail(String to, Notification notification) {
        if (to == null || to.isBlank()) {
            return;
        }

        String subject = notification.getTitle() != null ? notification.getTitle() : "Bank notification";

        String body = notification.getMessage() != null ? notification.getMessage() : "You have a new notification in internet banking";

        smtpMailSender.send(defaultFrom, to, subject, body);
    }
}
package com.example.bank.infrastructure.email;

import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpMailSender {
    private static final Logger log = LoggerFactory.getLogger(SmtpMailSender.class);
    private final JavaMailSender mailSender;
    public void send(String from, String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

        }catch(Exception e){
            log.error("Failed to send email to {}, {}", to, e);
        }
    }
}
package com.example.bank.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditLogger {

    public void log(String action, String details) {
        log.info("[AUDIT] action={} details={}", action, details);
    }

    public void logError(String action, String details, Throwable ex) {
        log.error("[AUDIT] action={} details={}", action, details, ex);
    }
}

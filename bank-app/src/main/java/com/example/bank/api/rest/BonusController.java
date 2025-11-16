package com.example.bank.api.rest;

import com.example.bank.domain.bonus.model.BonusAccount;
import com.example.bank.domain.bonus.service.BonusService;
import com.example.bank.domain.customer.model.CustomerProfile;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/bonus")
@RequiredArgsConstructor
public class BonusController {

    private final BonusService bonusService;

    @GetMapping
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BonusResponse> getBonus(@AuthenticationPrincipal CustomerProfile customer) {
        BonusAccount account = bonusService.getOrCreateBonusAccount(customer);

        BonusResponse resp = new BonusResponse();
        resp.setBalance(account.getBalance());
        resp.setUpdatedAt(account.getUpdatedAt());

        return ResponseEntity.ok(resp);
    }

    @Data
    public static class BonusResponse {
        private BigDecimal balance;
        private OffsetDateTime updatedAt;
    }
}

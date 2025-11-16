package com.example.bank.api.rest;

import com.example.bank.domain.currency.model.Currency;
import com.example.bank.domain.deposit.model.Deposit;
import com.example.bank.domain.deposit.model.DepositStatus;
import com.example.bank.domain.deposit.service.DepositService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @PostMapping("/api/deposits")
    @ResponseBody
    public ResponseEntity<DepositResponse> createDeposit(@RequestBody CreateDepositRequest request) {
        Deposit deposit = depositService.createDeposit(
                request.getAccountId(),
                request.getPrincipalAmount(),
                request.getCurrency(),
                request.getMonthlyInterest(),
                request.getTermMonths(),
                request.getEmail()
        );
        return ResponseEntity.status(201).body(mapToResponse(deposit));
    }

    @GetMapping("/api/deposits/my")
    @ResponseBody
    public ResponseEntity<List<DepositResponse>> getMyDeposits() {
        List<Deposit> deposits = depositService.getAllDeposits();
        List<DepositResponse> responses = deposits.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/deposits")
    @ResponseBody
    public ResponseEntity<List<DepositResponse>> getDepositsByAccount(@RequestParam Long accountId) {
        List<DepositResponse> responses = depositService.getAllDeposits().stream()
                .filter(d -> d.getAccount() != null && d.getAccount().getId().equals(accountId))
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/deposits/{id}")
    @ResponseBody
    public ResponseEntity<DepositResponse> getDeposit(@PathVariable Long id) {
        Optional<Deposit> depositOpt = depositService.getDeposit(id);
        return depositOpt
                .map(deposit -> ResponseEntity.ok(mapToResponse(deposit)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/deposits/{id}/close")
    @ResponseBody
    public ResponseEntity<DepositResponse> closeDeposit(@PathVariable Long id,
                                                        @RequestParam(required = false) String email) {
        Deposit closed = depositService.closeDeposit(id, email);
        return ResponseEntity.ok(mapToResponse(closed));
    }

    @GetMapping("/api/deposits/status/{status}")
    @ResponseBody
    public ResponseEntity<List<DepositResponse>> getDepositsByStatus(@PathVariable DepositStatus status) {
        List<Deposit> deposits = depositService.getDepositsByStatus(status);
        List<DepositResponse> responses = deposits.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/ui/deposits")
    public String getDepositsPage(Model model) {
        List<Deposit> deposits = depositService.getAllDeposits();
        model.addAttribute("deposits", deposits);
        return "deposits";
    }

    @PostMapping("/ui/deposits")
    public String createDepositPage(@RequestParam Long accountId,
                                    @RequestParam BigDecimal principalAmount,
                                    @RequestParam Currency currency,
                                    @RequestParam BigDecimal monthlyInterest,
                                    @RequestParam int termMonths,
                                    @RequestParam(required = false) String email) {
        depositService.createDeposit(accountId, principalAmount, currency, monthlyInterest, termMonths, email);
        return "redirect:/ui/deposits";
    }

    @Data
    public static class CreateDepositRequest {
        private Long accountId;
        private BigDecimal principalAmount;
        private Currency currency;
        private BigDecimal monthlyInterest;
        private int termMonths;
        private String email;
    }

    @Data
    public static class DepositResponse {
        private Long id;
        private Long accountId;
        private BigDecimal principalAmount;
        private Currency currency;
        private BigDecimal monthlyInterest;
        private int termMonths;
        private DepositStatus status;
        private OffsetDateTime openedAt;
        private OffsetDateTime closedAt;
    }

    private DepositResponse mapToResponse(Deposit deposit) {
        DepositResponse r = new DepositResponse();
        r.setId(deposit.getId());
        r.setAccountId(deposit.getAccount().getId());
        r.setPrincipalAmount(deposit.getPrincipalAmount());
        r.setCurrency(deposit.getCurrency());
        r.setMonthlyInterest(deposit.getMonthlyInterest());
        r.setTermMonths(deposit.getTermMonths());
        r.setStatus(deposit.getStatus());
        r.setOpenedAt(deposit.getOpenedAt());
        r.setClosedAt(deposit.getClosedAt());
        return r;
    }
}

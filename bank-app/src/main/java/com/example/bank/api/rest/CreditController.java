package com.example.bank.api.rest;

import com.example.bank.domain.credit.model.Credit;
import com.example.bank.domain.credit.model.CreditStatus;
import com.example.bank.domain.credit.model.CreditType;
import com.example.bank.domain.credit.service.CreditService;
import com.example.bank.domain.currency.model.Currency;
import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.customer.repository.CustomerProfileRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;
    private final CustomerProfileRepository customerProfileRepository;

    @PostMapping("/api/credits")
    @ResponseBody
    public ResponseEntity<?> createCredit(@RequestBody CreateCreditRequest request) {
        if (request.getCustomerId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Customer is required"));
        }

        CustomerProfile customer = customerProfileRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + request.getCustomerId()));

        Credit credit = creditService.createCredit(
                customer,
                request.getPrincipalAmount(),
                request.getCurrency(),
                request.getInterestRateAnnual(),
                request.getTermMonths(),
                request.getCreditType()
        );

        return ResponseEntity.status(201).body(mapToResponse(credit));
    }

    @GetMapping("/api/credits/my")
    @ResponseBody
    public ResponseEntity<List<CreditResponse>> getMyCredits() {
        List<Credit> credits = creditService.getAllCredits();
        List<CreditResponse> responses = credits.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/credits")
    @ResponseBody
    public ResponseEntity<List<CreditResponse>> getCreditsByCustomerId(@RequestParam Long customerId) {
        CustomerProfile customer = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        List<Credit> credits = creditService.getCreditsByCustomer(customer);
        List<CreditResponse> responses = credits.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/credits/{id}")
    @ResponseBody
    public ResponseEntity<CreditResponse> getCredit(@PathVariable Long id) {
        Optional<Credit> creditOpt = creditService.getCredit(id);
        return creditOpt
                .map(credit -> ResponseEntity.ok(mapToResponse(credit)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/credits/{id}/close")
    @ResponseBody
    public ResponseEntity<CreditResponse> closeCredit(@PathVariable Long id) {
        Credit closed = creditService.closeCredit(id);
        return ResponseEntity.ok(mapToResponse(closed));
    }

    @GetMapping("/api/credits/status/{status}")
    @ResponseBody
    public ResponseEntity<List<CreditResponse>> getCreditsByStatus(@PathVariable CreditStatus status,
                                                                   @RequestParam Long customerId) {
        CustomerProfile customer = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        List<CreditResponse> responses = creditService.getCreditsByCustomer(customer).stream()
                .filter(credit -> credit.getStatus() == status)
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/ui/credits")
    public String getCreditsPage(Model model) {
        List<Credit> credits = creditService.getAllCredits();
        model.addAttribute("credits", credits);
        return "credits";
    }

    @PostMapping("/ui/credits")
    public String createCreditPage(@RequestParam BigDecimal principalAmount,
                                   @RequestParam Currency currency,
                                   @RequestParam BigDecimal interestRateAnnual,
                                   @RequestParam int termMonths,
                                   @RequestParam CreditType creditType,
                                   @RequestParam Long customerId) {
        CustomerProfile customer = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        creditService.createCredit(customer, principalAmount, currency, interestRateAnnual, termMonths, creditType);
        return "redirect:/ui/credits";
    }

    @Data
    public static class CreateCreditRequest {
        private Long customerId;
        private BigDecimal principalAmount;
        private Currency currency;
        private BigDecimal interestRateAnnual;
        private int termMonths;
        private CreditType creditType;
    }

    @Data
    public static class CreditResponse {
        private Long id;
        private BigDecimal principalAmount;
        private Currency currency;
        private BigDecimal interestRateAnnual;
        private int termMonths;
        private CreditType creditType;
        private CreditStatus status;
        private OffsetDateTime createdAt;
        private OffsetDateTime closedAt;
    }

    private CreditResponse mapToResponse(Credit credit) {
        CreditResponse response = new CreditResponse();
        response.setId(credit.getId());
        response.setPrincipalAmount(credit.getPrincipalAmount());
        response.setCurrency(credit.getCurrency());
        response.setInterestRateAnnual(credit.getInterestRateAnnual());
        response.setTermMonths(credit.getTermMonths());
        response.setCreditType(credit.getCreditType());
        response.setStatus(credit.getStatus());
        response.setCreatedAt(credit.getCreatedAt());
        response.setClosedAt(credit.getClosedAt());
        return response;
    }
}

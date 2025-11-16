package com.example.bank.api.rest;

import com.example.bank.domain.currency.service.CurrencyService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/supported")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Set<String>> getSupported() {
        return ResponseEntity.ok(currencyService.supported());
    }

    @GetMapping("/base-rates")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, BigDecimal>> getBaseRates() {
        return ResponseEntity.ok(currencyService.baseRates());
    }

    @GetMapping("/rate/{from}/{to}")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BigDecimal> getRate(@PathVariable String from,
                                              @PathVariable String to) {
        return ResponseEntity.ok(currencyService.rate(from, to));
    }

    @PostMapping("/convert")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ConvertResponse> convert(@RequestBody ConvertRequest request) {
        BigDecimal result = currencyService.convert(
                request.getAmount(),
                request.getFrom(),
                request.getTo()
        );
        ConvertResponse resp = new ConvertResponse();
        resp.setResult(result);
        return ResponseEntity.ok(resp);
    }

    @Data
    public static class ConvertRequest {
        private BigDecimal amount;
        private String from;
        private String to;
    }

    @Data
    public static class ConvertResponse {
        private BigDecimal result;
    }
}

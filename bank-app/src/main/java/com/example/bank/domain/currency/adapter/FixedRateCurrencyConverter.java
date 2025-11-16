package com.example.bank.domain.currency.adapter;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

@Component
public class FixedRateCurrencyConverter implements CurrencyConverter {

    private static final Map<String, BigDecimal> BASE = Map.of(
            "KZT", BigDecimal.ONE,
            "USD", new BigDecimal("550.0000"),
            "RUB", new BigDecimal("6.0000")
    );

    private static final Set<String> SUPPORTED = Set.of("USD", "KZT", "RUB");

    @Override
    public BigDecimal convert(BigDecimal amount, String from, String to) {
        if (from == null || to == null) throw new IllegalArgumentException("Currency codes must not be null");
        if (from.equalsIgnoreCase(to)) return amount;
        BigDecimal r = rate(from, to);
        return amount.multiply(r).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal rate(String from, String to) {
        String f = from.toUpperCase();
        String t = to.toUpperCase();
        if (!SUPPORTED.contains(f) || !SUPPORTED.contains(t)) {
            throw new IllegalArgumentException("Unsupported currency pair: " + from + " -> " + to);
        }
        BigDecimal kztPerFrom = BASE.get(f);
        BigDecimal kztPerTo = BASE.get(t);
        return kztPerFrom.divide(kztPerTo, 4, RoundingMode.HALF_UP);
    }

    @Override
    public Set<String> supported() {
        return SUPPORTED;
    }

    @Override
    public Map<String, BigDecimal> baseRates() {
        return BASE;
    }
}
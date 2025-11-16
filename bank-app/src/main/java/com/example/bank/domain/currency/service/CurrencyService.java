package com.example.bank.domain.currency.service;

import com.example.bank.domain.currency.adapter.CurrencyConverter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Service
public class CurrencyService {

    private final CurrencyConverter converter;

    public CurrencyService(CurrencyConverter converter) {
        this.converter = converter;
    }

    public BigDecimal convert(BigDecimal amount, String from, String to) {
        return converter.convert(amount, from, to);
    }

    public BigDecimal rate(String from, String to) {
        return converter.rate(from, to);
    }

    public Set<String> supported() {
        return converter.supported();
    }

    public Map<String, BigDecimal> baseRates() {
        return converter.baseRates();
    }
}
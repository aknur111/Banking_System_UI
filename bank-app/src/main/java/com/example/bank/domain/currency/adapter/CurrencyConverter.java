package com.example.bank.domain.currency.adapter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface CurrencyConverter {

    BigDecimal convert(BigDecimal amount, String from, String to);
    BigDecimal rate(String from, String to);
    Set<String> supported();
    Map<String, BigDecimal> baseRates();
}
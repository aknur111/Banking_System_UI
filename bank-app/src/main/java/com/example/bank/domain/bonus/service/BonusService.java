package com.example.bank.domain.bonus.service;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.bonus.model.BonusAccount;
import com.example.bank.domain.bonus.model.CashbackRule;
import com.example.bank.domain.bonus.repository.BonusAccountRepository;
import com.example.bank.domain.bonus.repository.CashbackRuleRepository;
import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.notification.model.NotificationType;
import com.example.bank.domain.notification.service.NotificationService;
import com.example.bank.domain.transaction.model.TransactionType;
import com.example.bank.domain.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BonusService {

    private final BonusAccountRepository bonusAccountRepository;
    private final CashbackRuleRepository cashbackRuleRepository;
    private final TransactionService transactionService;
    private final NotificationService notificationService;


    @Transactional
    public BonusAccount getOrCreateBonusAccount(CustomerProfile customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer must not be null to create bonus account");
        }

        return bonusAccountRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    OffsetDateTime now = OffsetDateTime.now();
                    BonusAccount ba = BonusAccount.builder()
                            .customer(customer)
                            .balance(BigDecimal.ZERO)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return bonusAccountRepository.save(ba);
                });
    }

    @Transactional(readOnly = true)
    public Optional<BonusAccount> findBonusAccount(CustomerProfile customer) {
        if (customer == null) {
            return Optional.empty();
        }
        return bonusAccountRepository.findByCustomer(customer);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBonusBalance(CustomerProfile customer) {
        if (customer == null) {
            return BigDecimal.ZERO;
        }

        return bonusAccountRepository.findByCustomer(customer)
                .map(BonusAccount::getBalance)
                .orElse(BigDecimal.ZERO);
    }


    @Transactional
    public BigDecimal applyCashback(Account account,
                                    BigDecimal purchaseAmount,
                                    String category) {

        if (account == null || purchaseAmount == null || purchaseAmount.signum() <= 0) {
            return BigDecimal.ZERO;
        }

        CustomerProfile customer = account.getCustomer();
        if (customer == null) {
            return BigDecimal.ZERO;
        }

        List<CashbackRule> rules;
        if (category != null && !category.isBlank()) {
            rules = cashbackRuleRepository.findByCategoryAndActiveTrue(category);
        } else {
            rules = cashbackRuleRepository.findByActiveTrue();
        }

        if (rules.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal bestPercent = rules.stream()
                .filter(rule -> rule.getMinAmount() == null
                        || purchaseAmount.compareTo(rule.getMinAmount()) >= 0)
                .map(CashbackRule::getPercent)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        if (bestPercent.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal cashback = purchaseAmount
                .multiply(bestPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        if (cashback.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BonusAccount bonusAccount = getOrCreateBonusAccount(customer);
        bonusAccount.setBalance(bonusAccount.getBalance().add(cashback));
        bonusAccount.setUpdatedAt(OffsetDateTime.now());
        bonusAccount = bonusAccountRepository.save(bonusAccount);

        String description = "Cashback " + bestPercent + "% for category " +
                (category != null ? category : "ANY");

        transactionService.createTransaction(
                account.getId(),
                TransactionType.CASHBACK,
                cashback,
                account.getCurrency(),
                "IN",
                description,
                null
        );

        String payloadJson = String.format(
                "{\"cashback\":%s,\"currency\":\"%s\",\"category\":\"%s\",\"bonusBalance\":%s}",
                cashback.toPlainString(),
                account.getCurrency(),
                category != null ? category : "",
                bonusAccount.getBalance().toPlainString()
        );

        notificationService.notifyInApp(
                customer,
                NotificationType.BONUS,
                "Cashback accrued",
                "You received cashback " + cashback + " " + account.getCurrency() +
                        " for category " + (category != null ? category : "ANY") +
                        ". Bonus balance: " + bonusAccount.getBalance(),
                payloadJson
        );

        return cashback;
    }
}

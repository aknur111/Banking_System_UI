package com.example.bank.domain.deposit.service;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.repository.AccountRepository;
import com.example.bank.domain.currency.model.Currency;
import com.example.bank.domain.currency.service.CurrencyService;
import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.deposit.model.Deposit;
import com.example.bank.domain.deposit.model.DepositStatus;
import com.example.bank.domain.deposit.repository.DepositRepository;
import com.example.bank.domain.notification.facade.ReceiptFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepositService {

    private final DepositRepository depositRepository;
    private final AccountRepository accountRepository;
    private final CurrencyService currencyService;
    private final ReceiptFacade receiptFacade;

    public Deposit createDeposit(Long accountId,
                                 BigDecimal principalAmount,
                                 Currency currency,
                                 BigDecimal monthlyInterest,
                                 int termMonths,
                                 String emailTo) {

        if (accountId == null) {
            throw new IllegalArgumentException("AccountId must not be null");
        }
        if (principalAmount == null || principalAmount.signum() <= 0) {
            throw new IllegalArgumentException("Principal amount must be positive");
        }
        if (monthlyInterest == null || monthlyInterest.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Monthly interest must be >= 0");
        }
        if (termMonths <= 0) {
            throw new IllegalArgumentException("Term must be > 0 months");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        BigDecimal amountToSubtract = principalAmount;
        if (!currency.name().equals(account.getCurrency())) {
            amountToSubtract = currencyService.convert(
                    principalAmount,
                    currency.name(),
                    account.getCurrency()
            );
        }

        if (account.getBalance().compareTo(amountToSubtract) < 0) {
            throw new IllegalArgumentException("Insufficient balance in account: " + accountId);
        }

        account.setBalance(account.getBalance().subtract(amountToSubtract));
        accountRepository.save(account);

        Deposit deposit = Deposit.builder()
                .customer(account.getCustomer())
                .account(account)
                .principalAmount(principalAmount)
                .currency(currency)
                .monthlyInterest(monthlyInterest)
                .termMonths(termMonths)
                .status(DepositStatus.ACTIVE)
                .openedAt(OffsetDateTime.now())
                .build();

        Deposit saved = depositRepository.save(deposit);

        receiptFacade.sendDepositOpenedReceipt(saved, emailTo);

        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<Deposit> getDeposit(Long depositId) {
        return depositRepository.findById(depositId);
    }

    public Deposit closeDeposit(Long depositId, String emailTo) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new IllegalArgumentException("Deposit not found: " + depositId));

        if (deposit.getStatus() != DepositStatus.ACTIVE) {
            throw new IllegalArgumentException("Deposit is not active: " + depositId);
        }

        BigDecimal interest = deposit.getPrincipalAmount()
                .multiply(deposit.getMonthlyInterest())
                .multiply(BigDecimal.valueOf(deposit.getTermMonths()))
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = deposit.getPrincipalAmount().add(interest);

        Account account = deposit.getAccount();
        BigDecimal amountToAdd = totalAmount;
        if (!deposit.getCurrency().name().equals(account.getCurrency())) {
            amountToAdd = currencyService.convert(
                    totalAmount,
                    deposit.getCurrency().name(),
                    account.getCurrency()
            );
        }

        account.setBalance(account.getBalance().add(amountToAdd));
        accountRepository.save(account);

        deposit.setStatus(DepositStatus.CLOSED);
        deposit.setClosedAt(OffsetDateTime.now());

        Deposit saved = depositRepository.save(deposit);

        receiptFacade.sendDepositClosedReceipt(saved, emailTo);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Deposit> getDepositsByStatus(DepositStatus status) {
        return depositRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Deposit> getDepositsByCustomer(CustomerProfile customer) {
        return depositRepository.findByCustomerOrderByOpenedAtDesc(customer);
    }

    @Transactional(readOnly = true)
    public List<Deposit> getAllDeposits() {
        return depositRepository.findAll();
    }
}

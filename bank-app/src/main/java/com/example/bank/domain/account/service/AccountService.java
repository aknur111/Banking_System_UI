package com.example.bank.domain.account.service;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.repository.AccountRepository;
import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.customer.repository.CustomerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerProfileRepository customerProfileRepository;

    public Account createAccount(Long customerId, String currencyCode) {
        CustomerProfile customer = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        Account account = Account.builder()
                .customer(customer)
                .currency(currencyCode)
                .balance(BigDecimal.ZERO)
                .active(true)
                .build();

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<Account> getCustomerAccounts(Long customerId) {
        CustomerProfile customer = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        return accountRepository.findByCustomer(customer);
    }

    @Transactional(readOnly = true)
    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
    }

    public Account deposit(Long accountId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        Account account = getAccount(accountId);

        if (!account.isActive()) {
            throw new IllegalStateException("Account " + accountId + " is not active");
        }

        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    public Account withdraw(Long accountId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        Account account = getAccount(accountId);

        if (!account.isActive()) {
            throw new IllegalStateException("Account " + accountId + " is not active");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Not enough balance on account " + accountId);
        }

        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    public Account closeAccount(Long accountId) {
        Account account = getAccount(accountId);
        account.setActive(false);
        return accountRepository.save(account);
    }
}

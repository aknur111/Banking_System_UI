package com.example.bank.domain.transaction.service;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.repository.AccountRepository;
import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.customer.repository.CustomerProfileRepository;
import com.example.bank.domain.transaction.base.BasicTransactionProcessor;
import com.example.bank.domain.transaction.base.TransactionProcessor;
import com.example.bank.domain.transaction.decorator.BonusTransactionDecorator;
import com.example.bank.domain.transaction.decorator.CashbackTransactionDecorator;
import com.example.bank.domain.transaction.decorator.SuspiciousActivityDecorator;
import com.example.bank.domain.transaction.model.Transaction;
import com.example.bank.domain.transaction.model.TransactionStatus;
import com.example.bank.domain.transaction.model.TransactionType;
import com.example.bank.domain.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CustomerProfileRepository customerProfileRepository;

    private TransactionProcessor buildProcessor() {
        TransactionProcessor basic = new BasicTransactionProcessor(transactionRepository);
        TransactionProcessor suspicious = new SuspiciousActivityDecorator(basic, new BigDecimal("100000"));
        TransactionProcessor cashback = new CashbackTransactionDecorator(suspicious);
        return new BonusTransactionDecorator(cashback);
    }

    public Transaction createTransaction(
            Long accountId,
            TransactionType type,
            BigDecimal amount,
            String currency,
            String direction,
            String description,
            Card relatedCard
    ) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        Transaction tx = Transaction.builder()
                .account(account)
                .relatedCard(relatedCard)
                .type(type)
                .status(TransactionStatus.COMPLETED)
                .amount(amount)
                .currency(currency)
                .direction(direction)
                .description(description)
                .suspicious(false)
                .build();

        return buildProcessor().process(tx);
    }

    @Transactional
    public Transaction depositToAccount(Long accountId,
                                        BigDecimal amount,
                                        String currency,
                                        String description) {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found " + accountId));

        if (currency == null || currency.isBlank()) {
            currency = account.getCurrency();
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        return createTransaction(
                account.getId(),
                TransactionType.DEPOSIT,
                amount,
                currency,
                "IN",
                description,
                null
        );
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAccountTransactions(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        return transactionRepository.findByAccountOrderByCreatedAtDesc(account);
    }

    @Transactional(readOnly = true)
    public Transaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + id));
    }

    @Transactional
    public Transaction markAsSuspicious(Long id, String reason) {
        Transaction tx = getTransaction(id);
        tx.setSuspicious(true);
        tx.setSuspiciousReason(reason);
        return transactionRepository.save(tx);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getCustomerTransactions(CustomerProfile customer) {
        if (customer == null) {
            return List.of();
        }

        List<Account> accounts = accountRepository.findByCustomer(customer);
        if (accounts.isEmpty()) {
            return List.of();
        }

        return transactionRepository.findByAccountInOrderByCreatedAtDesc(accounts);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getCustomerTransactions(Long customerId) {
        CustomerProfile customer = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        return getCustomerTransactions(customer);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByCreatedAtDesc();
    }
}

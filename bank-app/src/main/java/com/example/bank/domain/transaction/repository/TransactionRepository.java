package com.example.bank.domain.transaction.repository;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountOrderByCreatedAtDesc(Account account);
    List<Transaction> findByAccountInOrderByCreatedAtDesc(List<Account> accounts);
    List<Transaction> findAllByOrderByCreatedAtDesc();
}
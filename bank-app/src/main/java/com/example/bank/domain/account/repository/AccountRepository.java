package com.example.bank.domain.account.repository;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.customer.model.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomer(CustomerProfile customer);
}
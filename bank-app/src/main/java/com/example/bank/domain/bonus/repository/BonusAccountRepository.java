package com.example.bank.domain.bonus.repository;

import com.example.bank.domain.bonus.model.BonusAccount;
import com.example.bank.domain.customer.model.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BonusAccountRepository extends JpaRepository<BonusAccount, Long> {

    Optional<BonusAccount> findByCustomer(CustomerProfile customer);
}
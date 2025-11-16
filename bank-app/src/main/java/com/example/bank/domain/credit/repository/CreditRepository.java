package com.example.bank.domain.credit.repository;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.credit.model.Credit;
import com.example.bank.domain.credit.model.CreditStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditRepository extends JpaRepository<Credit, Long> {

    List<Credit> findByCustomerOrderByCreatedAtDesc(CustomerProfile customer);

    List<Credit> findByStatus(CreditStatus status);
}
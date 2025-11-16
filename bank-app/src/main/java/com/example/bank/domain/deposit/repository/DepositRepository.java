package com.example.bank.domain.deposit.repository;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.deposit.model.Deposit;
import com.example.bank.domain.deposit.model.DepositStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, Long> {

    List<Deposit> findByCustomerOrderByOpenedAtDesc(CustomerProfile customer);

    List<Deposit> findByStatus(DepositStatus status);
}
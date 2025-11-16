package com.example.bank.domain.payment.repository;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.payment.model.Payment;
import com.example.bank.domain.payment.model.PaymentCategory;
import com.example.bank.domain.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByCustomerOrderByCreatedAtDesc(CustomerProfile customer);

    List<Payment> findByCustomerAndStatus(CustomerProfile customer, PaymentStatus status);

    List<Payment> findByCategory(PaymentCategory category);

    List<Payment> findByFromAccountOrderByCreatedAtDesc(Account fromAccount);

}

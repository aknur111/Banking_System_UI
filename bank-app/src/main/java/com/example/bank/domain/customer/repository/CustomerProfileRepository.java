package com.example.bank.domain.customer.repository;

import com.example.bank.domain.customer.model.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
    Optional<CustomerProfile> findByPhone(String phone);
}
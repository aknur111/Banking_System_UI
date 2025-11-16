package com.example.bank.domain.customer.repository;

import com.example.bank.domain.customer.model.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
}

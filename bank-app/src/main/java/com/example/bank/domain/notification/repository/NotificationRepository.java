package com.example.bank.domain.notification.repository;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByCustomerOrderByCreatedAtDesc(CustomerProfile customer);

    List<Notification> findByCustomerAndReadIsFalse(CustomerProfile customer);
}

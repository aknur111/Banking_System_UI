package com.example.bank.api.rest;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.customer.repository.CustomerProfileRepository;
import com.example.bank.domain.notification.model.Notification;
import com.example.bank.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController{
    private final NotificationService notificationService;
    private final CustomerProfileRepository customerProfileRepository;

    @GetMapping("/customer/{customer_id}")
    public ResponseEntity<List<Notification>> getCustomerNotifications(@PathVariable Long customer_id){
        CustomerProfile customer = customerProfileRepository.findById(customer_id).orElseThrow(() -> new IllegalArgumentException("Customer not found" + customer_id));
        return ResponseEntity.ok(notificationService.getCustomerNotifications(customer));
    }
    @GetMapping("/customer/{customerId}/unread")
    public ResponseEntity<List<Notification>> getUnreadCustomerNotifications(@PathVariable Long customerId){
        CustomerProfile customer = customerProfileRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found" + customerId));
        return ResponseEntity.ok(notificationService.getUnreadNotifications(customer));

    }



    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long notificationId){
        Notification updated = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(updated);
    }
    @PostMapping("/customer/{customerId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long customerId){
        CustomerProfile customer = customerProfileRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found" + customerId));
        notificationService.markAllAsRead(customer);
        return ResponseEntity.ok().build();
    }
}
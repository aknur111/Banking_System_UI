package com.example.bank.domain.notification.model;

import com.example.bank.domain.customer.model.CustomerProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private CustomerProfile customer;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 20)
    private String channel;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String message;

    @Column(columnDefinition = "text")
    private String payload;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if(createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}

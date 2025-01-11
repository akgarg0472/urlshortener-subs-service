package com.akgarg.subsservice.v1.subs;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@Setter
@Table(name = "subscriptions", indexes = {
        @Index(name = "idx_subscription_user_id", columnList = "user_id"),
        @Index(name = "idx_subscription_user_id_pack_id", columnList = "user_id,pack_id")
})
public class Subscription {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "pack_id", nullable = false)
    private String packId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private String description;

    @CreatedDate
    @Column(nullable = false)
    private long subscribedAt;

    @Column(nullable = false)
    private long expiresAt;

}

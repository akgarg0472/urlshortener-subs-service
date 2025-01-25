package com.akgarg.subsservice.v1.subs;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "subscriptions", indexes = {
        @Index(name = "idx_subscription_user_id", columnList = "user_id"),
        @Index(name = "idx_subscription_user_id_pack_id", columnList = "user_id,pack_id")
})
@SuppressWarnings("JpaDataSourceORMInspection")
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

    @Column(name = "subs_status", nullable = false)
    private String status;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean defaultSubscription;

    @CreatedDate
    @Column(nullable = false)
    private Long subscribedAt;

    @Column(nullable = false)
    private Long expiresAt;

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final Subscription that = (Subscription) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }

}

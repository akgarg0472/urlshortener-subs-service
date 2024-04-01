package com.akgarg.subsservice.subs.v1;

import com.akgarg.subsservice.plans.v1.plan.Plan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Entity(name = "subscriptions")
@Getter
@Setter
public class Subscription {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Plan plan;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String currency;

    private String description;

    @CreatedDate
    @Column(nullable = false)
    private long subscribedAt;

    @Column(nullable = false)
    private long expiresAt;

}

package com.akgarg.subsservice.plans.v1;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity(name = "subs_plans")
@Getter
@Setter
public class Plan {

    @Id
    private String id;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Double price;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String features;

    @Column(nullable = false)
    private String privileges;

    @Column(name = "is_visible", columnDefinition = "BOOLEAN DEFAULT true", nullable = false)
    private boolean visible;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false", nullable = false)
    private boolean deleted;

    @Column(columnDefinition = "BIGINT DEFAULT 2678400000", nullable = false)
    private long validity;

    @CreatedDate
    private long createdAt;

    @LastModifiedDate
    private long updatedAt;

}

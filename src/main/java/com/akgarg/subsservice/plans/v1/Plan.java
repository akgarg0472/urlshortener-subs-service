package com.akgarg.subsservice.plans.v1;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

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

    private long createdAt;
    private long updatedAt;

    public String[] getPrivileges() {
        return privileges.split(",");
    }

    public void setPrivileges(final String[] privileges) {
        this.privileges = String.join(",", privileges);
    }

    public String[] getFeatures() {
        return features.split("");
    }

    public void setFeatures(final String[] features) {
        this.features = String.join(",", features);
    }

}

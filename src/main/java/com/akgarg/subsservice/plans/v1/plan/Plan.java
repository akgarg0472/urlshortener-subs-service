package com.akgarg.subsservice.plans.v1.plan;

import com.akgarg.subsservice.plans.v1.privilege.PlanPrivilege;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    @OneToMany
    private List<PlanPrivilege> privileges = new ArrayList<>();

    @Column(name = "is_visible", columnDefinition = "BOOLEAN DEFAULT true", nullable = false)
    private boolean visible;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false", nullable = false)
    private boolean deleted;

    @Column(columnDefinition = "BIGINT DEFAULT 2678400000", nullable = false)
    private long validity;

    @Column(name = "created_at", nullable = false)
    private long createdAt;

    private long updatedAt;

    public String[] getFeatures() {
        return features.split("\\|");
    }

    public void setFeatures(final String[] features) {
        this.features = String.join("|", features);
    }

}

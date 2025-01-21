package com.akgarg.subsservice.v1.pack;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "subscription_pack")
@SuppressWarnings("JpaDataSourceORMInspection")
public class SubscriptionPack {

    @Id
    @Column(name = "id", nullable = false, unique = true, length = 100)
    private String id;

    @Column(name = "seq_order", nullable = false)
    private Integer order;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "validity_label", nullable = false)
    private String validityLabel;

    @Column(name = "validity_duration", nullable = false)
    private Long validityDuration;

    @Column(name = "is_visible", nullable = false)
    private boolean visible;

    @Column(name = "features", columnDefinition = "LONGTEXT")
    private String features;

    @Column(name = "privileges", length = 2000, nullable = false)
    private String privileges;

    @Column(name = "selected")
    private Boolean selected;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "is_default")
    private Boolean defaultPack;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }

    public List<String> getFeatures() {
        return List.of(features.split("~"));
    }

    public void setFeatures(final List<String> features) {
        this.features = String.join("~", features);
    }

    public List<String> getPrivileges() {
        return List.of(privileges.split("~"));
    }

    public void setPrivileges(final List<String> privileges) {
        this.privileges = String.join("~", privileges);
    }

}

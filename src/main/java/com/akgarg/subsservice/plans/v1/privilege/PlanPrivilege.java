package com.akgarg.subsservice.plans.v1.privilege;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "plan_privilege")
public class PlanPrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, name = "privilege_name")
    private String privilegeName;

    @Column(name = "created_at", nullable = false)
    private long createdAt;

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", privilegeName='" + privilegeName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

}

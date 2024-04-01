package com.akgarg.subsservice.v1.plans.privilege.db;

import com.akgarg.subsservice.v1.plans.privilege.PlanPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanPrivilegeRepository extends JpaRepository<PlanPrivilege, Integer> {

    Optional<PlanPrivilege> findByPrivilegeNameIgnoreCase(String privilegeName);

}
package com.akgarg.subsservice.plans.v1.privilege;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanPrivilegeRepository extends JpaRepository<PlanPrivilege, Integer> {

    Optional<PlanPrivilege> findByPrivilegeNameIgnoreCase(String privilegeName);

}
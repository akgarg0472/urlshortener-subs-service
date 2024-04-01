package com.akgarg.subsservice.v1.plans.privilege.db;

import com.akgarg.subsservice.v1.plans.privilege.PlanPrivilege;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlanPrivilegeDatabaseService {

    Optional<PlanPrivilege> findByPrivilegeNameIgnoreCase(String privilege);

    PlanPrivilege saveOrUpdate(PlanPrivilege planPrivilege);

    List<PlanPrivilege> findAllById(List<Integer> idList);

    Collection<PlanPrivilege> findAll();

}

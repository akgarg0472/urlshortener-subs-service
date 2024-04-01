package com.akgarg.subsservice.v1.plans.privilege.cache;

import com.akgarg.subsservice.v1.plans.privilege.PlanPrivilege;

import java.util.List;

public interface PlanPrivilegeCache {

    void addOrUpdatePlanPrivilege(PlanPrivilege planPrivilege);

    List<PlanPrivilege> getAllPlanPrivileges();

}

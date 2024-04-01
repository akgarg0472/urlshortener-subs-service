package com.akgarg.subsservice.plans.v1.privilege.cache;

import com.akgarg.subsservice.plans.v1.privilege.PlanPrivilege;

import java.util.List;

public interface PlanPrivilegeCache {

    void addOrUpdatePlanPrivilege(PlanPrivilege planPrivilege);

    List<PlanPrivilege> getAllPlanPrivileges();

}

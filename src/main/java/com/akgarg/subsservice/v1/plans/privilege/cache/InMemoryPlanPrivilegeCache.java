package com.akgarg.subsservice.v1.plans.privilege.cache;

import com.akgarg.subsservice.v1.plans.privilege.PlanPrivilege;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("dev")
@Component
public class InMemoryPlanPrivilegeCache implements PlanPrivilegeCache {

    @Override
    public void addOrUpdatePlanPrivilege(final PlanPrivilege planPrivilege) {

    }

    @Override
    public List<PlanPrivilege> getAllPlanPrivileges() {
        return null;
    }

}

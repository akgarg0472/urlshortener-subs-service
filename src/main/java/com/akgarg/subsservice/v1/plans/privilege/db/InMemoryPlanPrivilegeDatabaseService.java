package com.akgarg.subsservice.v1.plans.privilege.db;

import com.akgarg.subsservice.v1.plans.privilege.PlanPrivilege;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Profile("dev")
@Service
public class InMemoryPlanPrivilegeDatabaseService implements PlanPrivilegeDatabaseService {

    @Override
    public Optional<PlanPrivilege> findByPrivilegeNameIgnoreCase(final String privilege) {
        return Optional.empty();
    }

    @Override
    public PlanPrivilege saveOrUpdate(final PlanPrivilege planPrivilege) {
        return null;
    }

    @Override
    public List<PlanPrivilege> findAllById(final List<Integer> idList) {
        return Collections.emptyList();
    }

    @Override
    public Collection<PlanPrivilege> findAll() {
        return Collections.emptyList();
    }

}

package com.akgarg.subsservice.v1.plans.privilege.db;

import com.akgarg.subsservice.v1.plans.privilege.PlanPrivilege;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Profile("prod")
@Service
@RequiredArgsConstructor
public class MySQLPlanPrivilegeDatabaseService implements PlanPrivilegeDatabaseService {

    private final PlanPrivilegeRepository planPrivilegeRepository;

    @Override
    public Optional<PlanPrivilege> findByPrivilegeNameIgnoreCase(final String privilege) {
        return planPrivilegeRepository.findByPrivilegeNameIgnoreCase(privilege);
    }

    @Override
    public PlanPrivilege saveOrUpdate(final PlanPrivilege planPrivilege) {
        return planPrivilegeRepository.save(planPrivilege);
    }

    @Override
    public List<PlanPrivilege> findAllById(final List<Integer> idList) {
        return planPrivilegeRepository.findAllById(idList);
    }

    @Override
    public Collection<PlanPrivilege> findAll() {
        return planPrivilegeRepository.findAll();
    }

}

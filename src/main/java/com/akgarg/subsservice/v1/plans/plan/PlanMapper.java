package com.akgarg.subsservice.v1.plans.plan;

import com.akgarg.subsservice.v1.plans.privilege.PlanPrivilegeDto;

final class PlanMapper {

    private PlanMapper() {
        throw new IllegalStateException("utility class");
    }

    static PlanDTO toDto(final Plan plan) {
        return new PlanDTO(
                plan.getIcon(),
                plan.getId(),
                plan.getTitle(),
                plan.getDescription(),
                plan.getCode(),
                plan.getPrice(),
                plan.getFeatures(),
                plan.getPrivileges().stream().map(PlanPrivilegeDto::fromPlanPrivilege).toList()
        );
    }

}

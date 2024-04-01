package com.akgarg.subsservice.plans.v1.plan;

import com.akgarg.subsservice.plans.v1.privilege.PlanPrivilegeDto;

import java.util.List;

@SuppressWarnings("all")
public record PlanDTO(
        String icon,
        String id,
        String title,
        String description,
        String code,
        Double price,
        String[] features,
        List<PlanPrivilegeDto> privileges
) {
}
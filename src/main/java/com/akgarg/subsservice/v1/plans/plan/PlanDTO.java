package com.akgarg.subsservice.v1.plans.plan;

import com.akgarg.subsservice.v1.plans.privilege.PlanPrivilegeDto;

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
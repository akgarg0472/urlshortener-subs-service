package com.akgarg.subsservice.plans.v1.privilege;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlanPrivilegeDto(
        @JsonProperty("id")
        int id,
        @JsonProperty("privilege")
        String name
) {

    public static PlanPrivilegeDto fromPlanPrivilege(final PlanPrivilege privilege) {
        return new PlanPrivilegeDto(privilege.getId(), privilege.getPrivilegeName());
    }

}
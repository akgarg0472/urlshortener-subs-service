package com.akgarg.subsservice.subs.v1;

import com.akgarg.subsservice.plans.v1.plan.Plan;
import com.akgarg.subsservice.plans.v1.privilege.PlanPrivilege;
import com.akgarg.subsservice.plans.v1.privilege.PlanPrivilegeDto;

import java.util.List;

final class SubscriptionMapper {

    private SubscriptionMapper() {
        throw new IllegalStateException("utility class");
    }

    static SubscriptionDTO toDto(final Subscription subscription) {
        final SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        final Plan plan = subscription.getPlan();
        subscriptionDTO.setId(subscription.getId());
        subscriptionDTO.setPlanId(plan.getId());
        subscriptionDTO.setAmount(subscription.getAmount());
        subscriptionDTO.setCurrency(subscription.getCurrency());
        subscriptionDTO.setPrivileges(getPrivileges(plan.getPrivileges()));
        subscriptionDTO.setSubscribedAt(subscription.getSubscribedAt());
        subscriptionDTO.setExpiresAt(subscription.getExpiresAt());
        return subscriptionDTO;
    }

    private static List<PlanPrivilegeDto> getPrivileges(final List<PlanPrivilege> privileges) {
        return privileges
                .stream()
                .map(PlanPrivilegeDto::fromPlanPrivilege)
                .toList();
    }

}

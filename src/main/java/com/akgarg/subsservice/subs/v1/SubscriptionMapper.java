package com.akgarg.subsservice.subs.v1;

import com.akgarg.subsservice.plans.v1.Plan;

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
        subscriptionDTO.setPrivileges(plan.getPrivileges().split(","));
        subscriptionDTO.setSubscribedAt(subscription.getSubscribedAt());
        subscriptionDTO.setExpiresAt(subscription.getExpiresAt());
        return subscriptionDTO;
    }

}

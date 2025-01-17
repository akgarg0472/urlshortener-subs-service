package com.akgarg.subsservice.v1.pack;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SubscriptionPackDTO(
        String id,
        int order,
        String name,
        Double price,
        String validity,
        @JsonProperty("validity_duration") long validityDuration,
        String currency,
        String description,
        List<String> features,
        List<String> privileges,
        Boolean selected,
        @JsonProperty("default_pack") Boolean defaultPack
) {

    public static SubscriptionPackDTO fromSubscriptionPack(final SubscriptionPack pack) {
        return new SubscriptionPackDTO(
                pack.getId(),
                pack.getOrder(),
                pack.getName(),
                pack.getPrice(),
                pack.getValidityLabel(),
                pack.getValidityDuration(),
                pack.getCurrency(),
                pack.getDescription(),
                pack.getFeatures(),
                pack.getPrivileges(),
                pack.getSelected(),
                pack.getDefaultPack()
        );
    }

}

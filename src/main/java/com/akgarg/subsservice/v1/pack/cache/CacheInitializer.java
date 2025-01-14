package com.akgarg.subsservice.v1.pack.cache;

import com.akgarg.subsservice.v1.pack.SubscriptionPack;
import com.akgarg.subsservice.v1.pack.SubscriptionPackValidity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
//@Profile("dev")
@RequiredArgsConstructor
class CacheInitializer {

    private final SubscriptionPackCache subscriptionPackCache;

    @PostConstruct
    public void init() {
        final var subscriptionPack = new SubscriptionPack();
        subscriptionPack.setId("DEFAULT-FREE-001");
        subscriptionPack.setName("Free");
        subscriptionPack.setDescription("Default Free Subscription Pack");
        subscriptionPack.setPrice(0d);
        subscriptionPack.setCurrency("USD");
        subscriptionPack.setValidityLabel(SubscriptionPackValidity.DEFAULT.getLabel());
        subscriptionPack.setValidityDuration(SubscriptionPackValidity.DEFAULT.getDurationInMillis());
        subscriptionPack.setVisible(true);
        subscriptionPack.setFeatures(getFeatures());
        subscriptionPack.setPrivileges(getPrivileges());
        subscriptionPack.setDefaultPack(Boolean.TRUE);
        subscriptionPackCache.addOrUpdatePack("Initializer", subscriptionPack);
    }

    private List<String> getPrivileges() {
        return List.of(
                "short_url_50",
                "custom_alias_1"
        );
    }

    private List<String> getFeatures() {
        return List.of(
                "Up to 50 links",
                "Basic analytics",
                "Standard support"
        );
    }

}

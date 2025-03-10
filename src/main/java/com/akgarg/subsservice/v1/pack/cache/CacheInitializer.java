package com.akgarg.subsservice.v1.pack.cache;

import com.akgarg.subsservice.v1.pack.AnalyticMetricType;
import com.akgarg.subsservice.v1.pack.PackPrivilege;
import com.akgarg.subsservice.v1.pack.SubscriptionPack;
import com.akgarg.subsservice.v1.pack.SubscriptionPackValidity;
import com.akgarg.subsservice.v1.pack.db.SubscriptionPackDatabaseService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class CacheInitializer {

    private static final String VALUE_TRUE = ":true";
    private static final String VALUE_ADVANCED = ":advanced";

    private final SubscriptionPackDatabaseService subscriptionPackDatabaseService;
    private final SubscriptionPackCache subscriptionPackCache;
    private final Environment environment;

    @PostConstruct
    public void init() {
        final var freePack = getFreeSubscriptionPack();
        subscriptionPackCache.addOrUpdatePack(freePack);
        pushPackToDatabase(freePack);

        final var proPack = getProSubscriptionPack();
        subscriptionPackCache.addOrUpdatePack(proPack);
        pushPackToDatabase(proPack);

        final var enterprisePack = getEnterpriseSubscriptionPack();
        subscriptionPackCache.addOrUpdatePack(enterprisePack);
        pushPackToDatabase(enterprisePack);
    }

    private SubscriptionPack getFreeSubscriptionPack() {
        final var freePack = new SubscriptionPack();
        freePack.setOrder(1);
        freePack.setId("DEFAULT-FREE-001");
        freePack.setName("Free");
        freePack.setDescription("Free plan for basic usage");
        freePack.setPrice(0d);
        freePack.setCurrency("USD");
        freePack.setValidityLabel(SubscriptionPackValidity.DEFAULT.getLabel());
        freePack.setValidityDuration(SubscriptionPackValidity.DEFAULT.getDurationInMillis());
        freePack.setVisible(true);
        freePack.setFeatures(List.of("Up to 50 links", "Basic Analytics", "Standard Support"));
        freePack.setPrivileges(getFreePlanPrivileges());
        freePack.setSelected(false);
        freePack.setCreatedAt(System.currentTimeMillis());
        freePack.setDefaultPack(true);
        return freePack;
    }

    private SubscriptionPack getProSubscriptionPack() {
        final var proPack = new SubscriptionPack();
        proPack.setId("PAID-PRO-001");
        proPack.setOrder(2);
        proPack.setName("Pro");
        proPack.setDescription("Pro plan for advanced users");
        proPack.setPrice(19d);
        proPack.setCurrency("USD");
        proPack.setValidityLabel(SubscriptionPackValidity.MONTHLY.getLabel());
        proPack.setValidityDuration(SubscriptionPackValidity.MONTHLY.getDurationInMillis());
        proPack.setVisible(true);
        proPack.setFeatures(List.of("Up to 5000 links", "Advanced Analytics", "Custom Domains", "Priority Support"));
        proPack.setPrivileges(getProPlanPrivileges());
        proPack.setSelected(true);
        proPack.setCreatedAt(System.currentTimeMillis());
        proPack.setDefaultPack(false);
        return proPack;
    }

    private SubscriptionPack getEnterpriseSubscriptionPack() {
        final var enterprisePack = new SubscriptionPack();
        enterprisePack.setOrder(3);
        enterprisePack.setId("PAID-ENTERPRISE-001");
        enterprisePack.setName("Enterprise");
        enterprisePack.setDescription("Enterprise plan for organizations");
        enterprisePack.setPrice(99d);
        enterprisePack.setCurrency("USD");
        enterprisePack.setValidityLabel(SubscriptionPackValidity.MONTHLY.getLabel());
        enterprisePack.setValidityDuration(SubscriptionPackValidity.MONTHLY.getDurationInMillis());
        enterprisePack.setVisible(true);
        enterprisePack.setFeatures(List.of("Everything in Pro", "Unlimited links", "Advanced Security", "24/7 Premium Support"));
        enterprisePack.setPrivileges(getEnterprisePlanPrivileges());
        enterprisePack.setSelected(false);
        enterprisePack.setCreatedAt(System.currentTimeMillis());
        enterprisePack.setDefaultPack(false);
        return enterprisePack;
    }

    private List<String> getFreePlanPrivileges() {
        return List.of(
                PackPrivilege.SHORT_URL.value() + ":50",
                PackPrivilege.CUSTOM_ALIAS.value() + ":1",
                PackPrivilege.SECURITY.value() + ":basic",
                PackPrivilege.ANALYTICS.value() + ":basic"
        );
    }

    private List<String> getProPlanPrivileges() {
        return List.of(
                PackPrivilege.SHORT_URL.value() + ":5000",
                PackPrivilege.CUSTOM_ALIAS.value() + ":50",
                PackPrivilege.SECURITY.value() + VALUE_ADVANCED,
                PackPrivilege.ANALYTICS.value() + VALUE_ADVANCED,
                PackPrivilege.PREMIUM_SUPPORT.value() + VALUE_TRUE,
                PackPrivilege.ANALYTIC.value() + ":" + AnalyticMetricType.DEVICE_METRIC.metricName(),
                PackPrivilege.ANALYTIC.value() + ":" + AnalyticMetricType.GEOGRAPHY_METRIC.metricName()
        );
    }

    private List<String> getEnterprisePlanPrivileges() {
        return List.of(
                PackPrivilege.SHORT_URL.value() + ":unlimited",
                PackPrivilege.CUSTOM_ALIAS.value() + ":unlimited",
                PackPrivilege.SECURITY.value() + ":enterprise",
                PackPrivilege.ANALYTICS.value() + VALUE_ADVANCED,
                PackPrivilege.PREMIUM_SUPPORT.value() + VALUE_TRUE,
                PackPrivilege.PREMIUM_247_SUPPORT.value() + VALUE_TRUE,
                PackPrivilege.ANALYTIC.value() + ":" + AnalyticMetricType.ALL_METRICS.metricName()
        );
    }

    private void pushPackToDatabase(final SubscriptionPack pack) {
        if (Boolean.parseBoolean(environment.getProperty(
                "subscription.packs.initializer.push-default-packs-to-database",
                "false"))) {
            subscriptionPackDatabaseService.saveOrUpdatePack(pack);
        }
    }

}

package com.akgarg.subsservice.v1.pack;

public enum PackPrivilege {

    SHORT_URL("Short Url", "short_url"),
    CUSTOM_ALIAS("Custom Alias", "custom_alias"),
    API_ACCESS("API Access", "api_access"),
    SECURITY("Security", "security"),
    PREMIUM_SUPPORT("Premium Support", "premium_support"),
    ANALYTICS("Dashboard Analytics", "analytics"),
    ANALYTIC("", "analytic"),
    PREMIUM_247_SUPPORT("24/7 Premium Support", "247_premium_support");

    private final String key;
    private final String value;

    PackPrivilege(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String value() {
        return value;
    }

    public String key() {
        return key;
    }

}

package com.akgarg.subsservice.v1.pack;

public enum AnalyticMetricType {

    DEVICE_METRIC("device_metrics"),
    GEOGRAPHY_METRIC("geography_metrics"),
    URL_METRIC("url_metrics"),
    ALL_METRICS("*");

    private final String metricName;

    AnalyticMetricType(final String metricName) {
        this.metricName = metricName;
    }

    public String metricName() {
        return metricName;
    }

}

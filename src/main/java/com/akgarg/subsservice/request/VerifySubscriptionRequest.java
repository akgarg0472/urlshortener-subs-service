package com.akgarg.subsservice.request;

public record VerifySubscriptionRequest(
        String userId,
        String planId,
        String subsId
) {
}

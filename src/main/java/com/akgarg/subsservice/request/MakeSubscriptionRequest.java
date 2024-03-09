package com.akgarg.subsservice.request;

import java.util.Date;

public record MakeSubscriptionRequest(
        String userId,
        String planId,
        Double amount,
        Date startsAt,
        Date expiresAt,
        String description
) {
}
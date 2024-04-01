package com.akgarg.subsservice.kafka;

public record PaymentEvent(
        String userId,
        String planId,
        Long amount,
        String currency,
        String paymentGateway
) {
}
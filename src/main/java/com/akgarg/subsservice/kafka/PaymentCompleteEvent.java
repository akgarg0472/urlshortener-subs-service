package com.akgarg.subsservice.kafka;

public record PaymentCompleteEvent(
        String paymentId,
        String userId,
        String packId,
        Double amount,
        String currency,
        String paymentGateway,
        String email,
        String name
) {
}
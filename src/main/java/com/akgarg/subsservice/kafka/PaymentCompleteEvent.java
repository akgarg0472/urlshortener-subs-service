package com.akgarg.subsservice.kafka;

import static com.akgarg.subsservice.utils.SubsUtils.maskString;

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

    @Override
    public String toString() {
        return "{" +
                "paymentId='" + maskString(paymentId) + '\'' +
                ", userId='" + maskString(userId) + '\'' +
                ", packId='" + maskString(packId) + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", paymentGateway='" + paymentGateway + '\'' +
                ", email='" + maskString(email) + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
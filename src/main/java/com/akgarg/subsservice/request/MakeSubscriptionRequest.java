package com.akgarg.subsservice.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MakeSubscriptionRequest(

        @NotBlank(message = "user_id can't be null or empty")
        @JsonProperty("user_id")
        String userId,

        @NotBlank(message = "pack_id can't be null or empty")
        @JsonProperty("pack_id")
        String packId,

        @NotBlank(message = "payment_id can't be null or empty")
        @JsonProperty("payment_id")
        String paymentId,

        @Min(value = 1, message = "amount can't be null or empty")
        Double amount,

        @NotBlank(message = "currency can't be null or empty")
        String currency,

        @NotBlank(message = "description can't be null or empty")
        String description,

        @JsonProperty("email")
        String email,

        @JsonProperty("name")
        String name
) {
}

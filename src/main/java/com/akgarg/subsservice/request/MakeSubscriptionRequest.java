package com.akgarg.subsservice.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MakeSubscriptionRequest(

        @NotBlank(message = "user_id is mandatory & can't be empty")
        @JsonProperty("user_id")
        String userId,

        @NotBlank(message = "plan_id is mandatory & can't be empty")
        @JsonProperty("plan_id")
        String planId,

        @Min(value = 1, message = "amount can't be less than 1")
        Long amount,

        @NotBlank(message = "currency is mandatory & can't be empty")
        String currency,

        String description
) {
}

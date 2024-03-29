package com.akgarg.subsservice.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;

@SuppressWarnings("all")
public record CreatePlanRequest(

        @JsonProperty("plan_icon")
        @NotBlank(message = "Please provide valid plan_icon")
        String icon,

        @JsonProperty("plan_title")
        @NotBlank(message = "Please provide valid plan_icon")
        String title,

        @JsonProperty("plan_description")
        @NotBlank(message = "Please provide valid plan_icon")
        String description,

        @JsonProperty("plan_code")
        @NotBlank(message = "Please provide valid plan_code")
        String code,

        @JsonProperty("plan_price")
        @NotNull(message = "Please provide valid plan_price")
        @Min(value = 1, message = "Price should be minimum 1")
        Double price,

        @JsonProperty("plan_currency")
        @NotBlank(message = "Please provide valid plan_currency")
        String currency,

        @JsonProperty("plan_features")
        @NotNull(message = "Please provide valid plan_features")
        String[] features,

        @JsonProperty("plan_privileges")
        @NotNull(message = "Please provide valid plan_privileges")
        int[] privileges,

        @JsonProperty("is_visible")
        @NotNull(message = "Please provide valid is_visible")
        Boolean visible,

        @JsonProperty("plan_validity")
        @NotNull(message = "Please provide valid plan_validity in ms")
        @Min(value = 86400000L, message = "Validity should be minimum 86400000 ms (i.e. 1 day)")
        Long validity

) {

    @Override
    public String toString() {
        return "CreatePlanRequest{" +
                "icon='" + icon + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", features=" + Arrays.toString(features) +
                ", privileges=" + Arrays.toString(privileges) +
                ", visible=" + visible +
                ", validity=" + validity +
                '}';
    }

}

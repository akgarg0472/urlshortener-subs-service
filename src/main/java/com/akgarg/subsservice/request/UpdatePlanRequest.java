package com.akgarg.subsservice.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class UpdatePlanRequest {

    private String icon;
    private String title;
    private String description;
    private Double price;
    private String[] features;
    private int[] privileges;
    private Boolean visible;
    private Boolean deleted;
    private Long validity;

    @Override
    public String toString() {
        return "UpdatePlanRequest{" +
                "icon='" + icon + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", features=" + Arrays.toString(features) +
                ", privileges=" + Arrays.toString(privileges) +
                ", visible=" + visible +
                ", deleted=" + deleted +
                ", validity=" + validity +
                '}';
    }

}

package com.akgarg.subsservice.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePlanRequest {

    private String icon;
    private String title;
    private String description;
    private String code;
    private Double price;
    private String[] features;
    private int[] privileges;
    private Boolean visible;
    private Boolean deleted;
    private Long validity;

}

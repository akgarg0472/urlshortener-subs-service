package com.akgarg.subsservice.plans.v1;

import com.akgarg.subsservice.response.GetPlansResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions/plans")
public class PlansController {

    private final SubscriptionPlanService subscriptionPlanService;

    public PlansController(final SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @GetMapping("/")
    public ResponseEntity<GetPlansResponse> getPlans() {
        final GetPlansResponse response = subscriptionPlanService.getPlans();
        return ResponseEntity.status(response.statusCode()).body(response);
    }

}

package com.akgarg.subsservice.plans.v1;

import com.akgarg.subsservice.request.CreatePlanRequest;
import com.akgarg.subsservice.request.UpdatePlanRequest;
import com.akgarg.subsservice.response.CreatePlanResponse;
import com.akgarg.subsservice.response.DeletePlanResponse;
import com.akgarg.subsservice.response.GetPlansResponse;
import com.akgarg.subsservice.response.UpdatePlanResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions/plans")
public class PlansController {

    private final PlansService plansService;

    public PlansController(final PlansService plansService) {
        this.plansService = plansService;
    }

    @PostMapping
    public ResponseEntity<CreatePlanResponse> createPlan(@RequestBody @Valid final CreatePlanRequest request) {
        final CreatePlanResponse response = plansService.createPlan(request);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<GetPlansResponse> getPlans(@RequestParam(name = "limit", defaultValue = "2") final int limit) {
        final GetPlansResponse response = plansService.getPlans(limit);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

    @PatchMapping("/{planId}")
    public ResponseEntity<UpdatePlanResponse> updatePlan(
            @PathVariable(name = "planId") final String planId,
            @RequestBody final UpdatePlanRequest request
    ) {
        final UpdatePlanResponse response = plansService.updatePlan(planId, request);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<DeletePlanResponse> deletePlan(@PathVariable("planId") final String planId) {
        final DeletePlanResponse response = plansService.deletePlan(planId);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

}

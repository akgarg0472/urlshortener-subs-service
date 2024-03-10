package com.akgarg.subsservice.plans.v1;

import com.akgarg.subsservice.exception.BadRequestException;
import com.akgarg.subsservice.request.CreatePlanRequest;
import com.akgarg.subsservice.request.UpdatePlanRequest;
import com.akgarg.subsservice.response.CreatePlanResponse;
import com.akgarg.subsservice.response.DeletePlanResponse;
import com.akgarg.subsservice.response.GetPlansResponse;
import com.akgarg.subsservice.response.UpdatePlanResponse;
import com.akgarg.subsservice.utils.SubsUtils;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PlansService {

    private static final Logger LOGGER = LogManager.getLogger(PlansService.class);

    private final PlanRepository planRepository;

    public CreatePlanResponse createPlan(final CreatePlanRequest request) {
        return null;
    }

    public GetPlansResponse getPlans(final int limit) {
        final PageRequest pageRequest = PageRequest.of(
                0,
                limit,
                Sort.by("createdAt").descending()
        );

        final List<Plan> plans = planRepository.findByVisibleAndDeleted(
                true,
                false,
                pageRequest
        );

        final List<PlanDTO> planDTOS = plans.stream()
                .map(PlanMapper::toDto)
                .toList();

        return new GetPlansResponse(
                HttpStatus.OK.value(),
                planDTOS,
                null
        );
    }

    public UpdatePlanResponse updatePlan(final String planId, final UpdatePlanRequest request) {
        return null;
    }

    public DeletePlanResponse deletePlan(final String planId) {
        final String logId = SubsUtils.generateLogId();
        LOGGER.info("{} received request to delete plan: {}", logId, planId);

        final Plan plan = getPlanByPlanId(planId);
        plan.setDeleted(true);
        plan.setVisible(false);

        planRepository.save(plan);

        return new DeletePlanResponse(
                HttpStatus.OK.value(),
                "Plan deleted successfully with id=%s".formatted(planId)
        );
    }

    private Plan getPlanByPlanId(final String planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new BadRequestException(
                        new String[]{"No plan found with id=%s".formatted(planId)},
                        "Plan not found"
                ));
    }

}

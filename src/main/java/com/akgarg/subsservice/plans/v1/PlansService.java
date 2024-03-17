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
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PlansService {

    private static final Logger LOGGER = LogManager.getLogger(PlansService.class);

    private final PlanRepository planRepository;

    public CreatePlanResponse createPlan(final CreatePlanRequest request) {
        final String logId = SubsUtils.generateLogId();
        LOGGER.info("{} request received to create plan: {}", logId, request);

        final Optional<Plan> planByCode = planRepository.findByCode(request.code());

        if (planByCode.isPresent()) {
            LOGGER.error("{} plan already exists with code={}", logId, request.code());
            return new CreatePlanResponse(HttpStatus.CONFLICT.value(), "Plan already exists with code=" + request.code());
        }

        final Plan plan = new Plan();
        plan.setId(UUID.randomUUID().toString());
        plan.setIcon(request.icon());
        plan.setTitle(request.title());
        plan.setDescription(request.description());
        plan.setCode(request.code());
        plan.setPrice(request.price());
        plan.setFeatures(request.features());
        plan.setPrivileges(request.privileges());
        plan.setVisible(request.visible());
        plan.setValidity(request.validity());

        planRepository.save(plan);

        LOGGER.info("{} plan created successfully: {}", logId, plan);

        return new CreatePlanResponse(HttpStatus.CREATED.value(), "Plan created successfully");
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
        final String logId = SubsUtils.generateLogId();
        LOGGER.info("{} request received to update plan with id: {} -> {}", logId, planId, request);

        final Plan plan = getPlanByPlanId(planId);

        return new UpdatePlanResponse(HttpStatus.OK.value(), "Plan updated successfully");
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

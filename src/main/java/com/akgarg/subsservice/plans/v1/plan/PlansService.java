package com.akgarg.subsservice.plans.v1.plan;

import com.akgarg.subsservice.exception.BadRequestException;
import com.akgarg.subsservice.plans.v1.plan.cache.PlanCache;
import com.akgarg.subsservice.plans.v1.privilege.PlanPrivilege;
import com.akgarg.subsservice.plans.v1.privilege.PlanPrivilegeService;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class PlansService implements InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(PlansService.class);

    private final PlanRepository planRepository;
    private final PlanPrivilegeService planPrivilegeService;
    private final PlanCache planCache;

    public CreatePlanResponse createPlan(final CreatePlanRequest request) {
        final String logId = SubsUtils.generateLogId();
        LOGGER.info("{} request received to create plan: {}", logId, request);

        final Optional<Plan> planByCode = planRepository.findByCode(request.code());

        if (planByCode.isPresent()) {
            LOGGER.warn("{} plan already exists with code={}", logId, request.code());
            return new CreatePlanResponse(HttpStatus.CONFLICT.value(), "Plan already exists with code=" + request.code());
        }

        final List<PlanPrivilege> privileges = planPrivilegeService.getPlanPrivilegesById(request.privileges());

        final Plan plan = new Plan();
        plan.setId(UUID.randomUUID().toString().replace("-", ""));
        plan.setIcon(request.icon());
        plan.setTitle(request.title());
        plan.setDescription(request.description());
        plan.setCode(request.code());
        plan.setPrice(request.price());
        plan.setFeatures(request.features());
        plan.setPrivileges(privileges);
        plan.setVisible(request.visible());
        plan.setValidity(request.validity());
        plan.setCreatedAt(System.currentTimeMillis());

        final Plan savedPlan = planRepository.save(plan);

        LOGGER.info("{} plan created successfully: {}", logId, savedPlan);

        planCache.addOrUpdatePlan(savedPlan);

        return new CreatePlanResponse(HttpStatus.CREATED.value(), "Plan created successfully");
    }

    public GetPlansResponse getPlans(final int page, final int limit) {
        final String logId = SubsUtils.generateLogId();
        LOGGER.info("{} received request to get plans", logId);

        final PageRequest pageRequest = PageRequest.of(
                page,
                limit,
                Sort.by("createdAt").descending()
        );

        final List<Plan> plans = planCache.getAllPlans(
                page * limit,
                limit,
                true,
                false
        );

        if (plans.isEmpty()) {
            LOGGER.info("{} fetching plans from database", logId);

            final List<Plan> plansList = planRepository.findByVisibleAndDeleted(
                    true,
                    false,
                    pageRequest
            );
            plans.addAll(plansList);
        }

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
        LOGGER.info("{}: request received to update plan with id: {} -> {}", logId, planId, request);

        final Plan plan = getPlanByPlanId(planId);

        LOGGER.debug("{} existing plan -> {}", logId, plan);

        final boolean updated = updatePlanFields(plan, request);

        if (!updated) {
            LOGGER.info("{}: no plan updated because no field was provided", logId);
            return new UpdatePlanResponse(HttpStatus.NO_CONTENT.value(), "No fields provided for update. The plan remains unchanged");
        }

        plan.setUpdatedAt(System.currentTimeMillis());

        final Plan updatedPlan = planRepository.save(plan);

        LOGGER.info("{}: plan updated successfully", logId);

        planCache.addOrUpdatePlan(updatedPlan);

        return new UpdatePlanResponse(HttpStatus.OK.value(), "Plan updated successfully");
    }

    public DeletePlanResponse deletePlan(final String planId) {
        final String logId = SubsUtils.generateLogId();
        LOGGER.info("{}: received request to delete plan: {}", logId, planId);

        final Plan plan = getPlanByPlanId(planId);

        if (plan.isDeleted()) {
            return new DeletePlanResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Plan is already deleted"
            );
        }

        plan.setDeleted(true);
        plan.setVisible(false);

        final Plan deletedPlan = planRepository.save(plan);

        LOGGER.info("{}: plan deleted successfully", logId);

        planCache.addOrUpdatePlan(deletedPlan);

        return new DeletePlanResponse(
                HttpStatus.OK.value(),
                "Plan deleted successfully with id=%s".formatted(planId)
        );
    }

    private boolean updatePlanFields(final Plan plan, final UpdatePlanRequest request) {
        boolean updated = updateFieldIfDifferent(plan::setIcon, request.getIcon(), plan.getIcon());

        if (updateFieldIfDifferent(plan::setTitle, request.getTitle(), plan.getTitle())) {
            updated = true;
        }
        if (updateFieldIfDifferent(plan::setDescription, request.getDescription(), plan.getDescription())) {
            updated = true;
        }
        if (updateFieldIfDifferent(plan::setPrice, request.getPrice(), plan.getPrice())) {
            updated = true;
        }
        if (updateFieldIfDifferent(plan::setFeatures, request.getFeatures(), plan.getFeatures())) {
            updated = true;
        }
        if (updateFieldIfDifferent(
                plan::setPrivileges,
                planPrivilegeService.getPlanPrivilegesById(request.getPrivileges()),
                plan.getPrivileges()
        )) {
            updated = true;
        }
        if (updateFieldIfDifferent(plan::setVisible, request.getVisible(), plan.isVisible())) {
            updated = true;
        }
        if (updateFieldIfDifferent(plan::setValidity, request.getValidity(), plan.getValidity())) {
            updated = true;
        }
        if (updateFieldIfDifferent(plan::setDeleted, request.getDeleted(), plan.isDeleted())) {
            updated = true;
        }

        return updated;
    }

    private <T> boolean updateFieldIfDifferent(final Consumer<T> setter, final T newValue, final T oldValue) {
        if (newValue != null && !Objects.equals(newValue, oldValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    private Plan getPlanByPlanId(final String planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new BadRequestException(
                        new String[]{"No plan found with id=%s".formatted(planId)},
                        "Plan not found"
                ));
    }

    @Override
    public void afterPropertiesSet() {
        LOGGER.info("Populating plan cache");
        planRepository
                .findAll()
                .forEach(planCache::addOrUpdatePlan);
    }

}

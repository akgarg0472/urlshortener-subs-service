package com.akgarg.subsservice.v1.plans.privilege;

import com.akgarg.subsservice.exception.BadRequestException;
import com.akgarg.subsservice.response.AddPlanPrivilegeResponse;
import com.akgarg.subsservice.v1.plans.privilege.cache.PlanPrivilegeCache;
import com.akgarg.subsservice.v1.plans.privilege.db.PlanPrivilegeDatabaseService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanPrivilegeService {

    private static final Logger LOGGER = LogManager.getLogger(PlanPrivilegeService.class);

    private final PlanPrivilegeCache planPrivilegeCache;
    private final PlanPrivilegeDatabaseService planPrivilegeDatabaseService;

    public AddPlanPrivilegeResponse addPrivilege(final String privilege) {
        LOGGER.info("Request received to add plan privilege: {}", privilege);
        validatePlanPrivilege(privilege);

        final Optional<PlanPrivilege> privilegeOptional = planPrivilegeDatabaseService.findByPrivilegeNameIgnoreCase(privilege);

        if (privilegeOptional.isPresent()) {
            LOGGER.warn("Plan privilege '{}' already exists", privilege);
            return new AddPlanPrivilegeResponse(
                    HttpStatus.CONFLICT.value(),
                    "Plan privilege '%s' already exists".formatted(privilege)
            );
        }

        final PlanPrivilege planPrivilege = new PlanPrivilege();
        planPrivilege.setPrivilegeName(privilege);
        planPrivilege.setCreatedAt(System.currentTimeMillis());

        final PlanPrivilege savedPrivilege = planPrivilegeDatabaseService.saveOrUpdate(planPrivilege);

        LOGGER.info("Plan privilege created successfully: {}", savedPrivilege);

        planPrivilegeCache.addOrUpdatePlanPrivilege(savedPrivilege);

        return new AddPlanPrivilegeResponse(
                HttpStatus.CREATED.value(),
                "Plan privilege '%s' added successfully".formatted(privilege)
        );

    }

    public List<PlanPrivilege> getPlanPrivilegesById(final int[] ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }

        final List<Integer> idList = new ArrayList<>();
        for (final int id : ids) {
            idList.add(id);
        }
        return planPrivilegeDatabaseService.findAllById(idList);
    }

    public List<PlanPrivilegeDto> getPlanPrivileges() {
        final List<PlanPrivilege> privileges = planPrivilegeCache.getAllPlanPrivileges();

        if (privileges.isEmpty()) {
            privileges.addAll(this.planPrivilegeDatabaseService.findAll());
        }

        return privileges
                .stream()
                .map(PlanPrivilegeDto::fromPlanPrivilege)
                .toList();
    }

    private void validatePlanPrivilege(final String privilege) {
        if (privilege == null || privilege.isBlank()) {
            throw new BadRequestException(null, "Invalid privilege name: %s".formatted(privilege));
        }

        try {
            if (privilege.toUpperCase().startsWith(PrivilegeEnums.PrivilegePrefix.CUSTOM_ALIAS.name())) {
                final PrivilegeEnums.CustomAliasDuration customAliasDuration = getCustomAliasDuration(privilege);
                final long customAliasCount = getCustomAliasCount(privilege);
                LOGGER.info("Custom alias validated for custom alias: {}, {}", customAliasDuration, customAliasCount);
            } else if (privilege.toUpperCase().startsWith(PrivilegeEnums.PrivilegePrefix.STATISTICS.name())) {
                final String statisticsEndpoint = getStatisticsEndpoint(privilege);
                LOGGER.info("Custom alias validated for statistics: {}", statisticsEndpoint);
            } else {
                throw new BadRequestException(null, "Invalid privilege prefix");
            }
        } catch (Exception e) {
            LOGGER.error("Error validating privilege: {}", privilege, e);
            throw new BadRequestException(null, "Invalid privilege name: %s".formatted(privilege));
        }
    }

    private String getStatisticsEndpoint(final String privilege) {
        return privilege.substring(PrivilegeEnums.PrivilegePrefix.STATISTICS.name().length() + 1);
    }

    private long getCustomAliasCount(final String privilege) {
        final String customAliasCount = privilege.substring(
                privilege.lastIndexOf('_') + 1
        );
        return Long.parseLong(customAliasCount);
    }

    private PrivilegeEnums.CustomAliasDuration getCustomAliasDuration(final String privilege) {
        final String duration = privilege.substring(
                PrivilegeEnums.PrivilegePrefix.CUSTOM_ALIAS.name().length() + 1,
                privilege.lastIndexOf('_')
        );
        return PrivilegeEnums.CustomAliasDuration.valueOf(duration.toUpperCase());
    }

}

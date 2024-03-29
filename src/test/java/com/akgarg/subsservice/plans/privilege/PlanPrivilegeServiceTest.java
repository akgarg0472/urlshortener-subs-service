package com.akgarg.subsservice.plans.privilege;

import com.akgarg.subsservice.exception.BadRequestException;
import com.akgarg.subsservice.plans.v1.privilege.PlanPrivilegeRepository;
import com.akgarg.subsservice.plans.v1.privilege.PlanPrivilegeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class PlanPrivilegeServiceTest {

    @Mock
    private PlanPrivilegeRepository planPrivilegeRepository;

    @InjectMocks
    private PlanPrivilegeService privilegeService;

    @Test
    void test_addPrivilege_ShouldThrow_BadRequestException_When_NullPrivilege_Provided() {
        assertThat(planPrivilegeRepository).isNotNull();
        assertThat(privilegeService).isNotNull();
        assertThrows(BadRequestException.class, () -> privilegeService.addPrivilege(null));
    }

    @Test
    void test_addPrivilege_ShouldThrow_BadRequestException_When_InvalidPrivilege_Provided() {
        assertThat(planPrivilegeRepository).isNotNull();
        assertThat(privilegeService).isNotNull();
        assertThrows(BadRequestException.class, () -> privilegeService.addPrivilege("random_prefix"));
    }

    @Test
    void test_addPrivilege_ShouldSuccess_When_ValidCustomAliasPrivilege_Provided() {
        assertThat(planPrivilegeRepository).isNotNull();
        assertThat(privilegeService).isNotNull();
        privilegeService.addPrivilege("custom_alias_daily_10");
        privilegeService.addPrivilege("custom_alias_weekly_100");
        privilegeService.addPrivilege("custom_alias_monthly_" + Integer.MAX_VALUE);
    }

    @Test
    void test_addPrivilege_ShouldSuccess_When_ValidStatisticsPrivilege_Provided() {
        assertThat(planPrivilegeRepository).isNotNull();
        assertThat(privilegeService).isNotNull();
        privilegeService.addPrivilege("statistics_endpoint1");
        privilegeService.addPrivilege("statistics_endpoint2");
        privilegeService.addPrivilege("statistics_endpoint3");
    }

    @Test
    void test_addPrivilege_ShouldThrow_DataIntegrityViolationException_When_ValidDuplicatePrivilege_Provided() {
        assertThat(planPrivilegeRepository).isNotNull();
        assertThat(privilegeService).isNotNull();

        when(planPrivilegeRepository.save(Mockito.any()))
                .thenThrow(new DataIntegrityViolationException("could not execute statement [Duplicate entry *]"));

        assertThatThrownBy(() -> privilegeService.addPrivilege("custom_alias_daily_12"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Duplicate entry");
    }

}

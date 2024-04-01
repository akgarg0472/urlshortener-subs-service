package com.akgarg.subsservice.v1.plans.privilege;

import com.akgarg.subsservice.response.AddPlanPrivilegeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscriptions/plans/privileges")
@RequiredArgsConstructor
public class PlanPrivilegeController {

    private final PlanPrivilegeService planPrivilegeService;

    @PostMapping("/")
    public ResponseEntity<AddPlanPrivilegeResponse> addPlanPrivilege(@RequestBody final Map<String, String> requestBody) {
        final String privilegeName = requestBody.get("privilege_name");
        final var response = planPrivilegeService.addPrivilege(privilegeName);
        return ResponseEntity
                .status(response.statusCode())
                .body(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<PlanPrivilegeDto>> getPlanPrivileges() {
        return ResponseEntity.ok(planPrivilegeService.getPlanPrivileges());
    }

}

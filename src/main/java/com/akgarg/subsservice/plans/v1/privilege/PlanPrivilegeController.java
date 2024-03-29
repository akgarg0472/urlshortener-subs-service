package com.akgarg.subsservice.plans.v1.privilege;

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
    public ResponseEntity<String> addPlanPrivilege(@RequestBody final Map<String, String> requestBody) {
        final String privilegeName = requestBody.get("privilege_name");
        planPrivilegeService.addPrivilege(privilegeName);
        return ResponseEntity.ok("Privilege added successfully");
    }

    @GetMapping("/")
    public ResponseEntity<List<PlanPrivilegeDto>> getPlanPrivileges() {
        return ResponseEntity.ok(planPrivilegeService.getPlanPrivileges());
    }

}

package com.chen.ocsw.controller;

import com.chen.common.ApiResponse;
import com.chen.ocsw.dto.OcswCommand;
import com.chen.ocsw.dto.OcswResult;
import com.chen.ocsw.service.OcswWorkflowService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ocsw")
@RequiredArgsConstructor
public class OcswWorkflowController {
    private final OcswWorkflowService workflowService;

    @PostMapping("/window-admissions")
    @RequiresPermissions("ocsw:write")
    public ApiResponse<OcswResult> admit(@RequestBody OcswCommand command) {
        return ApiResponse.ok(workflowService.admitWindow(command));
    }

    @PostMapping("/outage-confirmations")
    @RequiresPermissions("ocsw:write")
    public ApiResponse<OcswResult> confirmOutage(@RequestBody OcswCommand command) {
        return ApiResponse.ok(workflowService.confirmOutage(command));
    }

    @PostMapping("/grounding-interlocks")
    @RequiresPermissions("ocsw:write")
    public ApiResponse<OcswResult> interlock(@RequestBody OcswCommand command) {
        return ApiResponse.ok(workflowService.interlockGrounding(command));
    }

    @PostMapping("/crew-entries")
    @RequiresPermissions("ocsw:write")
    public ApiResponse<OcswResult> enterCrew(@RequestBody OcswCommand command) {
        return ApiResponse.ok(workflowService.enterCrew(command));
    }

    @PostMapping("/defect-closures")
    @RequiresPermissions("ocsw:write")
    public ApiResponse<OcswResult> closeDefect(@RequestBody OcswCommand command) {
        return ApiResponse.ok(workflowService.closeDefect(command));
    }

    @PostMapping("/window-extensions")
    @RequiresPermissions("ocsw:write")
    public ApiResponse<OcswResult> extendWindow(@RequestBody OcswCommand command) {
        return ApiResponse.ok(workflowService.extendWindow(command));
    }

    @PostMapping("/evacuation-checks")
    @RequiresPermissions("ocsw:write")
    public ApiResponse<OcswResult> checkEvacuation(@RequestBody OcswCommand command) {
        return ApiResponse.ok(workflowService.checkEvacuation(command));
    }

    @PostMapping("/grounding-removals")
    @RequiresPermissions("ocsw:write")
    public ApiResponse<OcswResult> removeGrounding(@RequestBody OcswCommand command) {
        return ApiResponse.ok(workflowService.removeGrounding(command));
    }

    @PostMapping("/energization-reviews")
    @RequiresPermissions("ocsw:write")
    public ApiResponse<OcswResult> reviewEnergization(@RequestBody OcswCommand command) {
        return ApiResponse.ok(workflowService.reviewEnergization(command));
    }

    @PostMapping("/lifecycle-reconciliations")
    @RequiresPermissions("ocsw:write")
    public ApiResponse<OcswResult> reconcile(@RequestBody OcswCommand command) {
        return ApiResponse.ok(workflowService.reconcileLifecycle(command));
    }
}

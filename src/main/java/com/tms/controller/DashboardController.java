package com.tms.controller;

import com.tms.dto.response.ApiResponse;
import com.tms.dto.response.DashboardSummaryResponse;
import com.tms.dto.response.TaskResponse;
import com.tms.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary(user.getUsername())));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get tasks due in next 7 days")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getUpcoming(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getUpcomingTasks(user.getUsername())));
    }
}

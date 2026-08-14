package com.rentflow.dashboard.controller;

import com.rentflow.dashboard.dto.DashboardResponse;
import com.rentflow.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(
        name = "Dashboard",
        description = "RentFlow Dashboard APIs"
)
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Get dashboard summary"
    )
    @GetMapping
    public DashboardResponse getDashboard() {

        return dashboardService.getDashboard();
    }
}
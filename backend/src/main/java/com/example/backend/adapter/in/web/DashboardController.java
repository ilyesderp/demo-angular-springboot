package com.example.backend.adapter.in.web;

import com.example.backend.application.port.in.DashboardUseCase;
import com.example.backend.application.port.in.DashboardUseCase.Summary;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
class DashboardController {

    private final DashboardUseCase dashboard;

    DashboardController(DashboardUseCase dashboard) {
        this.dashboard = dashboard;
    }

    @GetMapping
    Summary summary(@AuthenticationPrincipal Jwt jwt) {
        return dashboard.summary(AuthenticatedUser.from(jwt).workspaceId());
    }
}

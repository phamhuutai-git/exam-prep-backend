package com.example.examprepbackend.controller.Admin;

import com.example.examprepbackend.common.BaseResponse;
import com.example.examprepbackend.dto.response.admin.AdminStatsResponse;
import com.example.examprepbackend.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<BaseResponse<AdminStatsResponse>> getDashboardStats() {
        AdminStatsResponse stats = adminDashboardService.getAdminStats();
        return ResponseEntity.ok(new BaseResponse<>(stats, "Get admin dashboard stats successfully"));
    }
}
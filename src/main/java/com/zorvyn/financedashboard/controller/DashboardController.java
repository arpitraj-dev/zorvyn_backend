package com.zorvyn.financedashboard.controller;

import com.zorvyn.financedashboard.dto.ApiResponseDTO;
import com.zorvyn.financedashboard.dto.CategorySummaryDTO;
import com.zorvyn.financedashboard.dto.DashboardSummaryDTO;
import com.zorvyn.financedashboard.dto.FinancialRecordResponseDTO;
import com.zorvyn.financedashboard.dto.MonthlyTrendDTO;
import com.zorvyn.financedashboard.security.annotation.ReadAccess;
import com.zorvyn.financedashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Dashboard Aggregation APIs.
 * All endpoints allow ADMIN, ANALYST, and VIEWER roles (read-only).
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /dashboard/summary
     * Returns total income, total expense, and net balance.
     */
    @GetMapping("/summary")
    @ReadAccess
    public ResponseEntity<ApiResponseDTO<DashboardSummaryDTO>> getSummary() {
        DashboardSummaryDTO summary = dashboardService.getSummary();
        return ResponseEntity.ok(
                ApiResponseDTO.success("Dashboard summary fetched successfully", summary));
    }

    /**
     * GET /dashboard/category-summary
     * Returns category-wise aggregation with total amount per category.
     */
    @GetMapping("/category-summary")
    @ReadAccess
    public ResponseEntity<ApiResponseDTO<List<CategorySummaryDTO>>> getCategorySummary() {
        List<CategorySummaryDTO> categorySummary = dashboardService.getCategorySummary();
        return ResponseEntity.ok(
                ApiResponseDTO.success("Category summary fetched successfully", categorySummary));
    }

    /**
     * GET /dashboard/recent
     * Returns recent transactions sorted by date descending.
     * 
     * @param limit 
     */
    @GetMapping("/recent")
    @ReadAccess
    public ResponseEntity<ApiResponseDTO<List<FinancialRecordResponseDTO>>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        
        int effectiveLimit = Math.min(Math.max(limit, 1), 50);
        
        List<FinancialRecordResponseDTO> recentRecords = dashboardService.getRecentTransactions(effectiveLimit);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Recent transactions fetched successfully", recentRecords));
    }

    /**
     * GET /dashboard/monthly-trends
     * Returns monthly income/expense trends.
     */
    @GetMapping("/monthly-trends")
    @ReadAccess
    public ResponseEntity<ApiResponseDTO<List<MonthlyTrendDTO>>> getMonthlyTrends() {
        List<MonthlyTrendDTO> trends = dashboardService.getMonthlyTrends();
        return ResponseEntity.ok(
                ApiResponseDTO.success("Monthly trends fetched successfully", trends));
    }
}

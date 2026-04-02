package com.zorvyn.financedashboard.service;

import com.zorvyn.financedashboard.dto.CategorySummaryDTO;
import com.zorvyn.financedashboard.dto.DashboardSummaryDTO;
import com.zorvyn.financedashboard.dto.FinancialRecordResponseDTO;
import com.zorvyn.financedashboard.dto.MonthlyTrendDTO;

import java.util.List;

/**
 * Service interface for dashboard aggregation APIs.
 */
public interface DashboardService {

    /**
     * Get overall summary: total income, expense, and net balance.
     */
    DashboardSummaryDTO getSummary();

    /**
     * Get category-wise aggregation with totals.
     */
    List<CategorySummaryDTO> getCategorySummary();

    /**
     * Get recent transactions (last N records).
     */
    List<FinancialRecordResponseDTO> getRecentTransactions(int limit);

    /**
     * Get monthly trends with income/expense totals per month.
     */
    List<MonthlyTrendDTO> getMonthlyTrends();
}

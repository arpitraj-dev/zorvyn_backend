package com.zorvyn.financedashboard.repository.projection;

/**
 * Interface-based projection for dashboard summary.
 * Used for single-query aggregation of income and expense totals.
 */
public interface DashboardSummaryProjection {

    Double getTotalIncome();

    Double getTotalExpense();
}

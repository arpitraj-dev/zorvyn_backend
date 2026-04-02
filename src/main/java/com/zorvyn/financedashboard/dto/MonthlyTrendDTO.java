package com.zorvyn.financedashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for monthly trend data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTrendDTO {

    private String month;  // Format: YYYY-MM
    private Double totalIncome;
    private Double totalExpense;
    private Double netBalance;
}

package com.zorvyn.financedashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for category-wise aggregation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySummaryDTO {

    private String category;
    private Double totalAmount;
    private Long recordCount;
}

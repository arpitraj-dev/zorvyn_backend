package com.zorvyn.financedashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wraps the paginated result of a filtered GET /records query.
 * Includes both the records for the current page and overall count metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilteredRecordsResponseDTO {

    /** Records on the current page */
    private List<FinancialRecordResponseDTO> records;

    /** Total number of records matching the filter (across all pages) */
    private long totalCount;

    /** Current zero-based page index */
    private int currentPage;

    /** Number of records per page requested */
    private int pageSize;

    /** Total number of pages */
    private int totalPages;
}

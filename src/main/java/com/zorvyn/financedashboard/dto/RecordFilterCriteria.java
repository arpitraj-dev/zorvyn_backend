package com.zorvyn.financedashboard.dto;

import com.zorvyn.financedashboard.entity.enums.RecordType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * for the GET /records endpoint.
 */
@Data
public class RecordFilterCriteria {

    // ── Filter fields ──────────────────────────────────────────────────────────

    /** Filter by record type: INCOME or EXPENSE */
    private RecordType type;

    /** Filter by category (case-insensitive partial match) */
    private String category;

    /** Search keyword (searches in notes and category, case-insensitive) */
    private String keyword;

    /** Filter records on or after this date (ISO format: yyyy-MM-dd) */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    /** Filter records on or before this date (ISO format: yyyy-MM-dd) */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    /** Minimum amount (inclusive) */
    @Positive(message = "minAmount must be greater than 0")
    private Double minAmount;

    /** Maximum amount (inclusive) */
    @Positive(message = "maxAmount must be greater than 0")
    private Double maxAmount;

    // ── Pagination fields ──────────────────────────────────────────────────────

    /** Zero-based page index (default: 0) */
    @jakarta.validation.constraints.Min(value = 0, message = "Page index must be >= 0")
    private int page = 0;

    /** Page size – max 100 results per page (default: 20) */
    @jakarta.validation.constraints.Min(value = 1, message = "Page size must be >= 1")
    @jakarta.validation.constraints.Max(value = 100, message = "Page size must be <= 100")
    private int size = 20;

    // ── Sorting fields ─────────────────────────────────────────────────────────

    private String sortBy = "date";

    private String order = "desc";

    // ── Cross-field validation ─────────────────────────────────────────────────

    @AssertTrue(message = "startDate must not be after endDate")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) return true;
        return !startDate.isAfter(endDate);
    }

    @AssertTrue(message = "minAmount must be less than or equal to maxAmount")
    public boolean isAmountRangeValid() {
        if (minAmount == null || maxAmount == null) return true;
        return minAmount <= maxAmount;
    }

    /**
     * Validates that sortBy is one of the allowed column names
     * to prevent SQL injection via the sort parameter.
     */
    @AssertTrue(message = "sortBy must be one of: id, amount, date, category, type")
    public boolean isSortByValid() {
        if (sortBy == null) return true;
        return sortBy.matches("id|amount|date|category|type");
    }

    @AssertTrue(message = "order must be 'asc' or 'desc'")
    public boolean isOrderValid() {
        if (order == null) return true;
        return order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc");
    }
}

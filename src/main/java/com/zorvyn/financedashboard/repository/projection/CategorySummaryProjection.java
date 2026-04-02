package com.zorvyn.financedashboard.repository.projection;

/**
 * Interface-based projection for category summary aggregation.
 * Spring Data JPA will automatically map query results to this interface.
 */
public interface CategorySummaryProjection {

    String getCategory();

    Double getTotalAmount();

    Long getRecordCount();
}

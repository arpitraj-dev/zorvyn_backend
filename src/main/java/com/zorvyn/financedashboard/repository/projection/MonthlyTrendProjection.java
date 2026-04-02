package com.zorvyn.financedashboard.repository.projection;

/**
 * Interface-based projection for monthly trend aggregation.
 * Spring Data JPA will automatically map query results to this interface.
 */
public interface MonthlyTrendProjection {

    String getMonth();

    String getType();

    Double getTotalAmount();
}

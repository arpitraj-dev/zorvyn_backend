package com.zorvyn.financedashboard.repository.projection;

import java.time.LocalDate;

/**
 * Interface-based projection for recent transactions.
 * Only fetches required fields, avoiding full entity load.
 */
public interface RecentTransactionProjection {

    Long getId();

    Double getAmount();

    String getType();

    String getCategory();

    LocalDate getDate();

    String getNotes();

    Long getUserId();

    String getUserName();
}

package com.zorvyn.financedashboard.repository;

import com.zorvyn.financedashboard.entity.FinancialRecord;
import com.zorvyn.financedashboard.entity.enums.RecordType;
import com.zorvyn.financedashboard.repository.projection.CategorySummaryProjection;
import com.zorvyn.financedashboard.repository.projection.DashboardSummaryProjection;
import com.zorvyn.financedashboard.repository.projection.MonthlyTrendProjection;
import com.zorvyn.financedashboard.repository.projection.RecentTransactionProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository
        extends JpaRepository<FinancialRecord, Long>,
                JpaSpecificationExecutor<FinancialRecord> {

    List<FinancialRecord> findByUserId(Long userId);

    // Soft Delete Queries

    /**
     * Find record by ID including soft-deleted records (for restore operation).
     */
    @Query("SELECT r FROM FinancialRecord r WHERE r.id = :id")
    Optional<FinancialRecord> findByIdIncludingDeleted(@Param("id") Long id);

    /**
     * Find all deleted records (for admin recovery purposes).
     */
    @Query(value = "SELECT * FROM financial_records WHERE is_deleted = true ORDER BY deleted_at DESC",
           nativeQuery = true)
    List<FinancialRecord> findAllDeleted();

    /**
     * Soft delete a record by ID.
     */
    @Modifying
    @Query("UPDATE FinancialRecord r SET r.isDeleted = true, r.deletedAt = CURRENT_TIMESTAMP WHERE r.id = :id")
    void softDeleteById(@Param("id") Long id);

    /**
     * Restore a soft-deleted record by ID.
     */
    @Modifying
    @Query("UPDATE FinancialRecord r SET r.isDeleted = false, r.deletedAt = null WHERE r.id = :id")
    void restoreById(@Param("id") Long id);

    // Dashboard Aggregation Queries with Interface Projections
    // Note: @Where clause on entity automatically excludes deleted records

    /**
     * Get dashboard summary in a single query using conditional aggregation.
     * Returns income and expense totals without multiple DB calls.
     */
    @Query("SELECT " +
           "COALESCE(SUM(CASE WHEN r.type = 'INCOME' THEN r.amount ELSE 0 END), 0) AS totalIncome, " +
           "COALESCE(SUM(CASE WHEN r.type = 'EXPENSE' THEN r.amount ELSE 0 END), 0) AS totalExpense " +
           "FROM FinancialRecord r")
    DashboardSummaryProjection getDashboardSummary();

    /**
     * Get category-wise aggregation with total amount and record count.
     * Uses interface projection for type-safe mapping.
     */
    @Query("SELECT r.category AS category, " +
           "SUM(r.amount) AS totalAmount, " +
           "COUNT(r) AS recordCount " +
           "FROM FinancialRecord r " +
           "GROUP BY r.category " +
           "ORDER BY SUM(r.amount) DESC")
    List<CategorySummaryProjection> getCategorySummary();

    /**
     * Get recent transactions with only required fields.
     * Uses projection to avoid loading full entities.
     */
    @Query("SELECT r.id AS id, " +
           "r.amount AS amount, " +
           "CAST(r.type AS string) AS type, " +
           "r.category AS category, " +
           "r.date AS date, " +
           "r.notes AS notes, " +
           "r.user.id AS userId, " +
           "r.user.name AS userName " +
           "FROM FinancialRecord r " +
           "ORDER BY r.date DESC")
    List<RecentTransactionProjection> findRecentTransactions(Pageable pageable);

    /**
     * Get monthly trends with income and expense totals.
     * Uses native query for MySQL DATE_FORMAT function.
     */
    @Query(value = "SELECT DATE_FORMAT(r.date, '%Y-%m') AS month, " +
                   "r.type AS type, " +
                   "SUM(r.amount) AS totalAmount " +
                   "FROM financial_records r " +
                   "WHERE r.is_deleted = false " +
                   "GROUP BY DATE_FORMAT(r.date, '%Y-%m'), r.type " +
                   "ORDER BY month DESC",
           nativeQuery = true)
    List<MonthlyTrendProjection> getMonthlyTrends();

    // Legacy methods (kept for backward compatibility)

    /**
     * @deprecated Use {@link #getDashboardSummary()} 
     */
    @Deprecated
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.type = :type")
    Double sumAmountByType(@Param("type") RecordType type);

    /**
     * @deprecated Use {@link #findRecentTransactions(Pageable)} 
     */
    @Deprecated
    @Query("SELECT r FROM FinancialRecord r ORDER BY r.date DESC")
    List<FinancialRecord> findRecentRecords(Pageable pageable);
}

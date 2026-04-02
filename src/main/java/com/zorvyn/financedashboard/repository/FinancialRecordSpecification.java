package com.zorvyn.financedashboard.repository;

import com.zorvyn.financedashboard.dto.RecordFilterCriteria;
import com.zorvyn.financedashboard.entity.FinancialRecord;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Criteria API Specification for dynamic, composable filtering
 * of {@link FinancialRecord} entities.
 *
 * <p>
 * All predicates are optional — only non-null filter values contribute
 * a WHERE clause. This ensures the generated SQL is always efficient and
 * avoids fetching all rows into memory for in-app filtering.
 * </p>
 */
public class FinancialRecordSpecification implements Specification<FinancialRecord> {

    private final RecordFilterCriteria criteria;

    public FinancialRecordSpecification(RecordFilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<FinancialRecord> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();

        // ── type = INCOME | EXPENSE ────────────────────────────────────────────
        if (criteria.getType() != null) {
            predicates.add(cb.equal(root.get("type"), criteria.getType()));
        }

        // ── category (case-insensitive contains) ───────────────────────────────
        if (criteria.getCategory() != null && !criteria.getCategory().isBlank()) {
            predicates.add(cb.like(
                    cb.lower(root.get("category")),
                    "%" + criteria.getCategory().toLowerCase() + "%"));
        }

        // ── keyword search (searches in notes AND category) ────────────────────
        if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
            String searchTerm = "%" + criteria.getKeyword().toLowerCase() + "%";
            Predicate categoryMatch = cb.like(cb.lower(root.get("category")), searchTerm);
            Predicate notesMatch = cb.like(cb.lower(root.get("notes")), searchTerm);
            predicates.add(cb.or(categoryMatch, notesMatch));
        }

        // ── date range ─────────────────────────────────────────────────────────
        if (criteria.getStartDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("date"), criteria.getStartDate()));
        }
        if (criteria.getEndDate() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("date"), criteria.getEndDate()));
        }

        // ── amount range ───────────────────────────────────────────────────────
        if (criteria.getMinAmount() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), criteria.getMinAmount()));
        }
        if (criteria.getMaxAmount() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("amount"), criteria.getMaxAmount()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}

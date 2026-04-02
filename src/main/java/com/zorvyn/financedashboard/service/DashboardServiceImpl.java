package com.zorvyn.financedashboard.service;

import com.zorvyn.financedashboard.dto.CategorySummaryDTO;
import com.zorvyn.financedashboard.dto.DashboardSummaryDTO;
import com.zorvyn.financedashboard.dto.FinancialRecordResponseDTO;
import com.zorvyn.financedashboard.dto.MonthlyTrendDTO;
import com.zorvyn.financedashboard.repository.FinancialRecordRepository;
import com.zorvyn.financedashboard.repository.projection.CategorySummaryProjection;
import com.zorvyn.financedashboard.repository.projection.DashboardSummaryProjection;
import com.zorvyn.financedashboard.repository.projection.MonthlyTrendProjection;
import com.zorvyn.financedashboard.repository.projection.RecentTransactionProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of DashboardService using optimized projection queries.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository financialRecordRepository;

    @Override
    public DashboardSummaryDTO getSummary() {
        log.info("Fetching dashboard summary using single-query projection");

        // Single query fetches both income and expense totals
        DashboardSummaryProjection projection = financialRecordRepository.getDashboardSummary();

        Double totalIncome = projection.getTotalIncome() != null ? projection.getTotalIncome() : 0.0;
        Double totalExpense = projection.getTotalExpense() != null ? projection.getTotalExpense() : 0.0;
        Double netBalance = totalIncome - totalExpense;

        log.info("Summary - Income: {}, Expense: {}, Net: {}", totalIncome, totalExpense, netBalance);

        return DashboardSummaryDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .build();
    }

    @Override
    public List<CategorySummaryDTO> getCategorySummary() {
        log.info("Fetching category-wise summary using projection");

        List<CategorySummaryProjection> projections = financialRecordRepository.getCategorySummary();

        return projections.stream()
                .map(p -> CategorySummaryDTO.builder()
                        .category(p.getCategory())
                        .totalAmount(p.getTotalAmount())
                        .recordCount(p.getRecordCount())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<FinancialRecordResponseDTO> getRecentTransactions(int limit) {
        log.info("Fetching {} most recent transactions using projection", limit);

        List<RecentTransactionProjection> projections = financialRecordRepository
                .findRecentTransactions(PageRequest.of(0, limit));

        return projections.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MonthlyTrendDTO> getMonthlyTrends() {
        log.info("Fetching monthly trends using projection");

        List<MonthlyTrendProjection> projections = financialRecordRepository.getMonthlyTrends();

        // Group by month and combine income/expense
        Map<String, MonthlyTrendDTO> monthMap = new HashMap<>();

        for (MonthlyTrendProjection p : projections) {
            String month = p.getMonth();
            String type = p.getType();
            Double amount = p.getTotalAmount() != null ? p.getTotalAmount() : 0.0;

            MonthlyTrendDTO trend = monthMap.computeIfAbsent(month, m ->
                    MonthlyTrendDTO.builder()
                            .month(m)
                            .totalIncome(0.0)
                            .totalExpense(0.0)
                            .netBalance(0.0)
                            .build());

            if ("INCOME".equals(type)) {
                trend.setTotalIncome(amount);
            } else if ("EXPENSE".equals(type)) {
                trend.setTotalExpense(amount);
            }
        }

        // Calculate net balance and return sorted list
        return monthMap.values().stream()
                .peek(t -> t.setNetBalance(t.getTotalIncome() - t.getTotalExpense()))
                .sorted((a, b) -> b.getMonth().compareTo(a.getMonth()))
                .collect(Collectors.toList());
    }

    /**
     * Maps RecentTransactionProjection to FinancialRecordResponseDTO.
     */
    private FinancialRecordResponseDTO mapToResponseDTO(RecentTransactionProjection p) {
        return FinancialRecordResponseDTO.builder()
                .id(p.getId())
                .amount(p.getAmount())
                .type(com.zorvyn.financedashboard.entity.enums.RecordType.valueOf(p.getType()))
                .category(p.getCategory())
                .date(p.getDate())
                .notes(p.getNotes())
                .userId(p.getUserId())
                .userName(p.getUserName())
                .build();
    }
}

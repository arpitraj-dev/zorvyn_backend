package com.zorvyn.financedashboard.dto;

import com.zorvyn.financedashboard.entity.enums.RecordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecordRequestDTO {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Type is required (INCOME, EXPENSE)")
    private RecordType type;

    @NotBlank(message = "Category must not be empty")
    private String category;

    @NotNull(message = "Date must not be null")
    private LocalDate date;

    private String notes;

    @NotNull(message = "User ID is required")
    private Long userId;
}

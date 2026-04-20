package com.anphan.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Budget configuration for a specific category")
public class BudgetDTO {

    @Schema(description = "Budget ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Target year", example = "2026")
    @Min(value = 2000, message = "Invalid year")
    private int year;

    @Schema(description = "Target month", example = "5")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private int month;

    @Schema(description = "Budget amount limit", example = "5000000")
    @NotNull
    private BigDecimal amount;

    @Schema(description = "Category name", accessMode = Schema.AccessMode.READ_ONLY)
    private String categoryName;

    @Schema(description = "Category type (INCOME or EXPENSE)", accessMode = Schema.AccessMode.READ_ONLY)
    private String categoryTypeName;

    @Schema(description = "Associated Category ID", example = "1")
    private Long categoryId;
}
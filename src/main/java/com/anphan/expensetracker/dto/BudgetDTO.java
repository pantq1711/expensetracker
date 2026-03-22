package com.anphan.expensetracker.dto;

import com.anphan.expensetracker.entity.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Data
public class BudgetDTO {
    private Long id;

    private int year, month;

    private BigDecimal amount;

    private String categoryName;

    private String categoryTypeName;

    private Long categoryId;
}

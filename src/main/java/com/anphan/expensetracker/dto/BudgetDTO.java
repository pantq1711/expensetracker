package com.anphan.expensetracker.dto;

import com.anphan.expensetracker.entity.Category;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Data
public class BudgetDTO {
    private Long id;

    @Min(value = 2000, message = "Nam khong hop le")
    private int year;

    @Min(value = 1, message = "Thang phai tu 1 - 12")
    @Max(value = 12, message = "Thang phai tu 1 - 12")
    private int month;

    @NotNull
    private BigDecimal amount;


    private String categoryName;

    private String categoryTypeName;

    private Long categoryId;
}

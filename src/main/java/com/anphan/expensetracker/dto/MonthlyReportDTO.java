package com.anphan.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@AllArgsConstructor
public class MonthlyReportDTO {

    private YearMonth period;

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;
}

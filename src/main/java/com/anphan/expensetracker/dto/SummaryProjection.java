package com.anphan.expensetracker.dto;

import java.math.BigDecimal;

public interface SummaryProjection {
    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();

    default BigDecimal getBalance(){
        BigDecimal income = (getTotalIncome() == null) ? BigDecimal.ZERO : getTotalIncome();
        BigDecimal expense = (getTotalExpense() == null) ? BigDecimal.ZERO : getTotalExpense();
        return income.subtract(expense);
    }
}

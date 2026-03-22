package com.anphan.expensetracker.dto;

import java.math.BigDecimal;

public interface SummaryProjection {
    public BigDecimal getTotalIncome();

    public BigDecimal getTotalExpense();

    default BigDecimal getBalace(){
        BigDecimal income = (getTotalIncome() == null) ? BigDecimal.ZERO : getTotalIncome();
        BigDecimal expense = (getTotalExpense() == null) ? BigDecimal.ZERO : getTotalExpense();
        return income.subtract(expense);
    }
}

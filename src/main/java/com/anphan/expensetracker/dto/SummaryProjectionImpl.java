package com.anphan.expensetracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryProjectionImpl implements SummaryProjection{
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
}

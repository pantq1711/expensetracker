package com.anphan.expensetracker.dto;

import java.math.BigDecimal;

public interface FilterReportProjection {
    BigDecimal getTotal();
    Long getCount();
}

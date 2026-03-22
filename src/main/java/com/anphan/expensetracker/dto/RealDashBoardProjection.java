package com.anphan.expensetracker.dto;

import java.math.BigDecimal;

public interface RealDashBoardProjection {
    BigDecimal getAmountMonth1();
    BigDecimal getAmountMonth2();
    default BigDecimal Diff(){
        BigDecimal countMonth1 = (getAmountMonth1() == null) ? BigDecimal.ZERO : getAmountMonth1();
        BigDecimal countMonth2 = (getAmountMonth2() == null) ? BigDecimal.ZERO : getAmountMonth2();
        return countMonth1.subtract(countMonth2).abs();
    }
}

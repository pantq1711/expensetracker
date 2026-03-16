package com.anphan.expensetracker.dto;

import com.anphan.expensetracker.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private Long id;

    private BigDecimal amount;

    private LocalDate date;

    private Transaction.TransactionType type;

    private String note;

    private Long CategoryId;

}

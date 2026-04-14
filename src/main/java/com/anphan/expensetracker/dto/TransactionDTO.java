package com.anphan.expensetracker.dto;

import com.anphan.expensetracker.entity.Transaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private Long id;

    @NotNull(message = "So tien ko duoc null")
    @Positive(message = "So tien phai > 0")
    private BigDecimal amount;

    @NotNull(message = "Ngay khong duoc null")
    private LocalDate date;

    @NotNull
    private Transaction.TransactionType type;

    @Size(max = 255)
    private String note;

    private Long categoryId;

}

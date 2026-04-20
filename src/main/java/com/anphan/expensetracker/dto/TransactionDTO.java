package com.anphan.expensetracker.dto;

import com.anphan.expensetracker.entity.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Details of an income or expense transaction")
public class TransactionDTO {

    @Schema(description = "Transaction ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Transaction amount", example = "150000")
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Schema(description = "Date of the transaction", example = "2026-04-20")
    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @Schema(description = "Type of transaction (INCOME or EXPENSE)", example = "EXPENSE")
    @NotNull
    private Transaction.TransactionType type;

    @Schema(description = "Note or description for the transaction", example = "Lunch with friends")
    @Size(max = 255)
    private String note;

    @Schema(description = "Associated Category ID", example = "1")
    private Long categoryId;
}
package com.anphan.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletCreateDTO {
    @NotBlank
    private String name;

    private BigDecimal budget;
}
package com.anphan.expensetracker.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletResponseDTO {
    private Long id;
    private String name;
    private BigDecimal balance;
    private BigDecimal budget;
    private BigDecimal availableBalance;
}
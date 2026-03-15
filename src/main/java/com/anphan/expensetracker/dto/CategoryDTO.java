package com.anphan.expensetracker.dto;

import com.anphan.expensetracker.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;

    @NotBlank
    private String name;
    private String colorHex;
    private Category.CategoryType categoryType;
}

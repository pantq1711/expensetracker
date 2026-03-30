package com.anphan.expensetracker.dto;

import com.anphan.expensetracker.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "Ten category ko duoc de trong")
    @Size(max = 100)
    private String name;
    private String colorHex;

    @NotNull(message = "loai category khong duoc null")
    private Category.CategoryType categoryType;
}

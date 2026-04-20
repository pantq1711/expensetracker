package com.anphan.expensetracker.dto;

import com.anphan.expensetracker.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Category details for organizing transactions")
public class CategoryDTO {

    @Schema(description = "Category ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Display name of the category", example = "Food & Dining")
    @NotBlank(message = "Category name cannot be blank")
    @Size(max = 100)
    private String name;

    @Schema(description = "Hex color code for UI rendering", example = "#FF5733")
    private String colorHex;

    @Schema(description = "Type of category", example = "EXPENSE")
    @NotNull(message = "Category type cannot be null")
    private Category.CategoryType categoryType;
}
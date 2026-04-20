package com.anphan.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Personal note details")
public class NoteDTO {

    @Schema(description = "Note ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Title of the note", example = "Grocery List")
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @Schema(description = "Detailed content", example = "Buy milk, eggs, and bread")
    @Size(max = 2000)
    private String content;

    @Schema(description = "Indicates if the note is pinned to the top", example = "true")
    private boolean pinned;
}
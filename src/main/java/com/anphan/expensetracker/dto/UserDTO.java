package com.anphan.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Public user profile information")
public class UserDTO {

    @Schema(description = "User ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Full name", example = "An Phan")
    private String name;

    @Schema(description = "Email address", example = "anphan@example.com")
    private String email;
}
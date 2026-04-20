package com.anphan.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Credentials for user authentication")
public class LoginRequest {

    @Schema(description = "Registered email address", example = "anphan@example.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Account password", example = "password123")
    @NotBlank
    private String password;
}
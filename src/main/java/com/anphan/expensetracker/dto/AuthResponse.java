package com.anphan.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Authentication token payload")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    @Schema(description = "JWT Access Token for authorized requests", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Token used to request a new Access Token without re-login", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;
}
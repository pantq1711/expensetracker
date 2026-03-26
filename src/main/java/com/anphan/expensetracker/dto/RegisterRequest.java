package com.anphan.expensetracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Ten khong duoc de trong")
    private String name;

    @NotBlank
    @Email(message = "Email khong dung dinh dang")
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;
}

package com.maskilometros.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(

        @NotBlank(message = "Email is required")
        @Email(message = "Email address must be a valid email")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}

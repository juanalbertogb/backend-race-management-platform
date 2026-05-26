package com.maskilometros.backend.dto;

public record UserAdminResponseDto(
        Long id,
        String name,
        String email,
        String mobileNumber
) {
}

package com.maskilometros.backend.dto;

import com.maskilometros.backend.entity.Role;

import java.time.Instant;

public record RegisterResponseDto(
        String name,
        String email,
        String mobileNumber,
        Role role,
        Instant createdAt

) {

}

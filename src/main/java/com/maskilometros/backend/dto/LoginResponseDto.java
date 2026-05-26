package com.maskilometros.backend.dto;

public record LoginResponseDto(
        String message,
        UserDto userDto,
        String jwtToKen
) {
}

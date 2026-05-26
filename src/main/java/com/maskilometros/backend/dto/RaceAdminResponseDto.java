package com.maskilometros.backend.dto;

import java.time.LocalDateTime;

public record RaceAdminResponseDto(
        Long id,
        String name,
        LocalDateTime raceDate
) {
}

package com.maskilometros.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record RaceResponseDto(
        Long id,
        String name,
        String description,
        String location,
        LocalDateTime raceDate,
        BigDecimal price,
        RaceStatusEnum status,
        Instant createdAt,
        Integer maxParticipants,
        Integer availableSlots,
        Boolean soldOut
) {
}

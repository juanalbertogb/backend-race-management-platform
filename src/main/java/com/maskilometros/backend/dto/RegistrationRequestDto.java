package com.maskilometros.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistrationRequestDto(
        @NotNull(message = "User id is required")
        @Positive(message = "User id can not be negative")
        Long userId,
        @NotNull(message = "Race id is required")
        @Positive(message = "Race id can not be negative")
        Long raceId
) {
}

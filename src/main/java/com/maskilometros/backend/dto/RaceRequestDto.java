package com.maskilometros.backend.dto;

import jakarta.validation.constraints.*;
import org.aspectj.bridge.IMessage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RaceRequestDto(

        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Maximum size for name is 255 characters")
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Maximum size for description is 500 characters")
        String description,

        @NotBlank(message = "Location is required")
        @Size(max = 100)
        String location,

        @NotNull(message = "Race date is required")
        @Future(message = "Race date must be in future")
        LocalDateTime raceDate,

        @NotNull(message = "Price is required")
        @DecimalMin("0.0")
        BigDecimal price,

        @NotNull(message = "Maximum of participants is required")
        @Positive(message = "Maximum of participants cannot be negative")
        Integer maxParticipants


) {
}

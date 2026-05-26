package com.maskilometros.backend.dto;

import java.time.LocalDateTime;

public record RegistrationAdminResponseDto(
        Long id,
        RegistrationStatusEnum status,
        String bibNumber,
        UserAdminResponseDto user,
        LocalDateTime cancellationDate
) {
}

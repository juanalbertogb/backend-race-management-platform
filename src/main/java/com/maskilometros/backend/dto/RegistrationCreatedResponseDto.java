package com.maskilometros.backend.dto;

import java.time.Instant;

public record RegistrationCreatedResponseDto(
        Long id,
        RegistrationStatusEnum status,
        String bibNumber,
        String userName,
        String raceName,
        Instant paymentDeadline,
        PaymentCreateResponseDto paymentCreateResponseDto
) {
}

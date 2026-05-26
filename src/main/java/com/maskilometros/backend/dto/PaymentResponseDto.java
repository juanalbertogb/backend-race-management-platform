package com.maskilometros.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponseDto(
        Long id,
        PaymentStatusEnum status,
        BigDecimal amount,
        String currency,
        String provider,
        Instant paidAt,
        Instant refundedAt,
        Instant failedAt
) {
}

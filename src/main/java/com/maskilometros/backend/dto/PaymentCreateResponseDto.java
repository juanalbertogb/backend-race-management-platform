package com.maskilometros.backend.dto;

public record PaymentCreateResponseDto(
        Long paymentId,
        PaymentStatusEnum paymentStatus) {
}

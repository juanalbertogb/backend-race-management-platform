package com.maskilometros.backend.dto;

import java.time.Instant;

public record ErrorResponseDto(
        String path, int status, String message,
        Instant timestamp, String traceId) {
}

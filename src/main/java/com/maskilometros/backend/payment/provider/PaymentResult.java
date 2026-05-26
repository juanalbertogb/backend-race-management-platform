package com.maskilometros.backend.payment.provider;

public record PaymentResult(
        boolean success,
        String transactionId,
        String providerMessage,
        String provider
) {
}

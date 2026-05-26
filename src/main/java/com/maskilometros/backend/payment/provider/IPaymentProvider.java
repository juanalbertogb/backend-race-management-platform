package com.maskilometros.backend.payment.provider;

import com.maskilometros.backend.entity.Payment;

public interface IPaymentProvider {

    PaymentResult processPayment(Payment payment);
}

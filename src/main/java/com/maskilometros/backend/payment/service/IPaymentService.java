package com.maskilometros.backend.payment.service;

import com.maskilometros.backend.dto.PaymentCreateResponseDto;
import com.maskilometros.backend.dto.PaymentResponseDto;
import com.maskilometros.backend.entity.Payment;
import com.maskilometros.backend.entity.Registration;

public interface IPaymentService {

    PaymentCreateResponseDto createPayment(Registration registration);

    PaymentResponseDto completePayment(Long paymentId);

    PaymentResponseDto retryPayment(Long paymentId);
}

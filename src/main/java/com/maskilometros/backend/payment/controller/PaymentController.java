package com.maskilometros.backend.payment.controller;

import com.maskilometros.backend.dto.PaymentResponseDto;
import com.maskilometros.backend.payment.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class  PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/{paymentId}/complete")
    public ResponseEntity<PaymentResponseDto> completePayment(@PathVariable Long paymentId){
        PaymentResponseDto paymentResponseDto = paymentService.completePayment(paymentId);
        return ResponseEntity.ok(paymentResponseDto);
    }

    @PostMapping("/{paymentId}/retry")
    public ResponseEntity<PaymentResponseDto> retryPayment(@PathVariable Long paymentId){
        PaymentResponseDto paymentResponseDto = paymentService.retryPayment(paymentId);

        return ResponseEntity.ok(paymentResponseDto);
    }
}

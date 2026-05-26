package com.maskilometros.backend.payment.service.impl;

import com.maskilometros.backend.dto.PaymentCreateResponseDto;
import com.maskilometros.backend.dto.PaymentResponseDto;
import com.maskilometros.backend.dto.PaymentStatusEnum;
import com.maskilometros.backend.dto.RegistrationStatusEnum;
import com.maskilometros.backend.entity.Payment;
import com.maskilometros.backend.entity.Registration;
import com.maskilometros.backend.exception.InvalidPaymentStateException;
import com.maskilometros.backend.exception.ResourceAlreadyExistsException;
import com.maskilometros.backend.exception.ResourceNotFoundException;
import com.maskilometros.backend.payment.provider.PaymentResult;
import com.maskilometros.backend.payment.service.IPaymentService;
import com.maskilometros.backend.payment.provider.impl.FakePaymentProvider;
import com.maskilometros.backend.repository.PaymentRepository;
import com.maskilometros.backend.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final RegistrationRepository registrationRepository;
    private final FakePaymentProvider fakePaymentProvider;

    @Override
    public PaymentCreateResponseDto createPayment(Registration registration) {
        Payment p = new Payment();
        p.setStatus(PaymentStatusEnum.PENDING);
        p.setRegistration(registration);
        p.setAmount(registration.getRace().getPrice());
        p.setCurrency("MXN");
        p.setIdempotencyKey(UUID.randomUUID().toString());
        paymentRepository.save(p);
        return transformEntityToCreateDto(p);
    }

    @Override
    public PaymentResponseDto completePayment(Long paymentId) {

        Payment payment = paymentRepository.findByIdWithRegistration(paymentId).
                orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() == PaymentStatusEnum.COMPLETED) {
            throw new ResourceAlreadyExistsException("Payment is already paid");
        } else if (payment.getStatus() == PaymentStatusEnum.REFUNDED) {
            throw new ResourceAlreadyExistsException("Payment is refunded");
        } else if (payment.getRegistration().getStatus() == RegistrationStatusEnum.CANCELLED) {
            throw new InvalidPaymentStateException("You are not register, you should first register to be access to pay");
        }

        PaymentResult paymentResult = fakePaymentProvider.processPayment(payment);

        processPaymentResult(payment, paymentResult);

        return transformEntityToDto(payment);
    }

    @Override
    public PaymentResponseDto retryPayment(Long paymentId) {
        Payment payment = paymentRepository.findByIdWithRegistration(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found, you should register before to try to pay"));

        validatePaymentRetry(payment);

        //mock provider call
        PaymentResult paymentResult = fakePaymentProvider.processPayment(payment);
        processPaymentResult(payment, paymentResult);

        return transformEntityToDto(payment);
    }

    private void processPaymentResult(Payment payment, PaymentResult paymentResult) {

        payment.setExternalPaymentId(paymentResult.transactionId());
        payment.setProviderMessage(paymentResult.providerMessage());
        payment.setProvider(paymentResult.provider());
        if (paymentResult.success()) {
            payment.setStatus(PaymentStatusEnum.COMPLETED);
            payment.setPaidAt(Instant.now());
            Registration r = payment.getRegistration();
            r.setStatus(RegistrationStatusEnum.PAID);
            r.setPaymentDeadline(null);

            Integer maxBibNumber = registrationRepository.findMaxBibNumberByRaceId(r.getRace().getId());
            int nextBibNumber = maxBibNumber==null?1:maxBibNumber+1;
            r.setBibNumber(String.valueOf(nextBibNumber));
        } else {
            payment.setStatus(PaymentStatusEnum.FAILED);
            payment.setFailedAt(Instant.now());
        }
    }

    private void validatePaymentRetry(Payment payment) {

        if (payment.getStatus() != PaymentStatusEnum.FAILED) {
            throw new InvalidPaymentStateException("Can not retry payment for status " + payment.getStatus());
        }
        Registration registration = payment.getRegistration();
        if (registration.getStatus() == RegistrationStatusEnum.CANCELLED) {
            throw new InvalidPaymentStateException("Registration is cancelled");
        }

        Instant paymentDeadline = registration.getPaymentDeadline();
        if (paymentDeadline != null && paymentDeadline.isBefore(Instant.now())) {
            throw new InvalidPaymentStateException("Payment deadline has expired");
        }
    }

    private PaymentResponseDto transformEntityToDto(Payment payment) {
        return new PaymentResponseDto(payment.getId(), payment.getStatus(), payment.getAmount(), payment.getCurrency(),
                payment.getProvider(), payment.getPaidAt(), payment.getRefundedAt(), payment.getFailedAt());
    }

    private PaymentCreateResponseDto transformEntityToCreateDto(Payment payment) {
        return new PaymentCreateResponseDto(payment.getId(), payment.getStatus());
    }
}

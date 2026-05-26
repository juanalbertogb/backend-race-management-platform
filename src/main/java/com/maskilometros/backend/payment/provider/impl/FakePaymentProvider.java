package com.maskilometros.backend.payment.provider.impl;

import com.maskilometros.backend.entity.Payment;
import com.maskilometros.backend.payment.provider.IPaymentProvider;
import com.maskilometros.backend.payment.provider.PaymentResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class FakePaymentProvider implements IPaymentProvider {


    @Override
    public PaymentResult processPayment(Payment payment) {

        Random random = new Random();

        boolean success = random.nextBoolean();

        List<String> providers = List.of("Stripe","Mercado Pago","BBVA");

        String providerRandom = providers.get(random.nextInt(providers.size()));

        if(success){
            return new PaymentResult(true,
                    UUID.randomUUID().toString(),
                    "Payment completed successfully",providerRandom);
        }

        List<String> errors = List.of(
                "Card declined",
                "Insufficient funds",
                "Payment provider timeout",
                "Fraud suspected",
                "Invalid card number",
                "Payment provider unavailable"
        );
        String randomError = errors.get(random.nextInt(errors.size()));
        return new PaymentResult(false,
                UUID.randomUUID().toString(),
                randomError,providerRandom);
    }
}

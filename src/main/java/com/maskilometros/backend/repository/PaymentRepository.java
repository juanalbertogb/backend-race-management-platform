package com.maskilometros.backend.repository;

import com.maskilometros.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRegistrationId(Long registrationId);

    Optional<Payment> findByIdWithRegistration(@Param("paymentId") Long paymentId);


}

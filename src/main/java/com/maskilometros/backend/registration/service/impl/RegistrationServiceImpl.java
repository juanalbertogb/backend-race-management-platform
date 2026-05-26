package com.maskilometros.backend.registration.service.impl;

import com.maskilometros.backend.dto.*;
import com.maskilometros.backend.entity.MasKilometrosUser;
import com.maskilometros.backend.entity.Payment;
import com.maskilometros.backend.entity.Race;
import com.maskilometros.backend.entity.Registration;
import com.maskilometros.backend.exception.*;
import com.maskilometros.backend.payment.refund.RefundCalculator;
import com.maskilometros.backend.payment.refund.RefundDecision;
import com.maskilometros.backend.payment.service.IPaymentService;
import com.maskilometros.backend.registration.service.IRegistrationService;
import com.maskilometros.backend.repository.MasKilometrosUserRepository;
import com.maskilometros.backend.repository.PaymentRepository;
import com.maskilometros.backend.repository.RaceRepository;
import com.maskilometros.backend.repository.RegistrationRepository;
import com.maskilometros.backend.specification.RegistrationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationServiceImpl implements IRegistrationService {

    private final MasKilometrosUserRepository userRepository;
    private final RaceRepository raceRepository;
    private final RegistrationRepository registrationRepository;
    private final PaymentRepository paymentRepository;
    private final IPaymentService paymentService;
    private final RefundCalculator refundCalculator;

    @Override
    public RegistrationCreatedResponseDto registerToRace(Long raceId, String email) {

        Race race = searchRaceById(raceId);
        MasKilometrosUser user = searchUserByEmail(email);

        validateStatus(race);
        validateDate(race);

        Optional<Registration> registrationExist = searchByUserIdAndRaceId(user.getId(), race.getId());

        Registration registration;
        if (registrationExist.isPresent()) {
            registration = registrationExist.get();
            boolean reactivated = canBeReactivated(registration);
            if (reactivated) {
                reactivateRegistration(race, registration);
                PaymentCreateResponseDto paymentResponseDto = reactivatedPayment(registration.getId());
                return transformEntityToCreatedDto(registration,paymentResponseDto);
            } else if (registration.getStatus() == RegistrationStatusEnum.PENDING_PAYMENT && !isPaymentDeadlineExpired(registration)) {
                throw new ResourceAlreadyExistsException("User is already registered for the race: " + race.getName()
                        + " with pending payment");
            } else if (registration.getStatus() == RegistrationStatusEnum.PAID) {
                throw new ResourceAlreadyExistsException("User is already registered for the race: " + race.getName());
            }

        }

        validateCapacity(race);
        registration = new Registration();
        registration.setUser(user);
        registration.setRace(race);
        registration.setPaymentDeadline(Instant.now().plus(15, ChronoUnit.MINUTES));
        Registration registrationSaved = registrationRepository.save(registration);
        PaymentCreateResponseDto payment = paymentService.createPayment(registration);
        return transformEntityToCreatedDto(registrationSaved, payment);
    }



    @Override
    public RegistrationResponseDto cancelRegistration(Long registrationId, String email) {

        Registration registration = searchRegistrationById(registrationId);
        MasKilometrosUser user = searchUserByEmail(email);

        if (!registration.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("User has not register a registration with id:" + registrationId);
        }

        Race race = registration.getRace();

        if (registration.getStatus() == RegistrationStatusEnum.CANCELLED) {
            return transformEntityToDto(registration);
        }


        if (registration.getStatus() == RegistrationStatusEnum.PAID) {
            Payment payment = paymentRepository.findByRegistrationId(registration.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found for the registration id: "+registration.getId()));
            RefundDecision decision = refundCalculator.calculate(race, payment);
            applyRefundDecision(payment, decision);
        }

        registration.setStatus(RegistrationStatusEnum.CANCELLED);
        registration.setCancellationDate(LocalDateTime.now());
        registration.setPaymentDeadline(null);

        return transformEntityToDto(registration);
    }

    @Override
    public List<RegistrationAdminResponseDto> getRegistrationsAdmin(Long raceId, RegistrationStatusEnum status) {

        searchRaceById(raceId);

        Specification<Registration> spec = Specification.where(RegistrationSpecification.joinFetchUser())
                .and(RegistrationSpecification.hasId(raceId));

        if (status != null) {
            spec = spec.and(RegistrationSpecification.hasStatus(status));
        }

        List<Registration> registrations = registrationRepository.findAll(spec);
        return registrations.stream().map(this::transformEntityAdminToDto).collect(Collectors.toList());
    }

    @Override
    public List<RegistrationResponseDto> searchMyRegistrations(String email) {

        List<Registration> registrations = registrationRepository.fetchAllUsersByUserEmail(email);
        return registrations.stream().map(this::transformEntityToDto).collect(Collectors.toList());
    }


    @Override
    public void expirePendingRegistrations(){
        registrationRepository.cleanByStatusAndPaymentDeadline(RegistrationStatusEnum.CANCELLED,
                        LocalDateTime.now(), RegistrationStatusEnum.PENDING_PAYMENT, Instant.now());
    }

    private Optional<Registration> searchByUserIdAndRaceId(Long userId, Long raceId) {
        return registrationRepository.findByUserIdAndRaceId(userId, raceId);
    }

    private MasKilometrosUser searchUserByEmail(String email) {
        return userRepository.fetchUserWithRoleByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Race searchRaceById(Long id) {
        return raceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Race not found with id:" + id));
    }

    private Registration searchRegistrationById(Long registrationId) {
        return registrationRepository.fetchUserWithRaceById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + registrationId));
    }

    private void validateCapacity(Race race) {
        long amount = registrationRepository.countByRaceIdAndStatusIdIn(race.getId(),
                List.of(RegistrationStatusEnum.PAID, RegistrationStatusEnum.PENDING_PAYMENT));

        if (amount >= race.getMaxParticipants()) {
            throw new RaceFullException("No slots available for this race");

        }

    }

    private void applyRefundDecision(Payment payment, RefundDecision decision){
        payment.setStatus(decision.status());
        payment.setRefundedAt(Instant.now());
        payment.setRefundedAmount(decision.refundedAmount());
        payment.setRefundReason(decision.reason());
        payment.setRefundType(decision.refundType());

    }

    private boolean canBeReactivated(Registration registration) {

        return (registration.getStatus() == RegistrationStatusEnum.CANCELLED) ||
                (registration.getStatus() == RegistrationStatusEnum.PENDING_PAYMENT && isPaymentDeadlineExpired(registration));
    }

    private void reactivateRegistration(Race race, Registration registration) {
        validateCapacity(race);
        registration.setStatus(RegistrationStatusEnum.PENDING_PAYMENT);
        registration.setCancellationDate(null);
        registration.setPaymentDeadline(Instant.now().plus(15, ChronoUnit.MINUTES));
//        registrationRepository.save(registration);
    }

    private PaymentCreateResponseDto reactivatedPayment(Long registrationId) {

        Payment p = paymentRepository.findByRegistrationId(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with registration id:"+registrationId));

        p.setStatus(PaymentStatusEnum.PENDING);
        p.setIdempotencyKey(UUID.randomUUID().toString());
        p.setFailedAt(null);
        return new PaymentCreateResponseDto(p.getId(),p.getStatus());
    }


    private void validateStatus(Race race) {
        if (race.getStatus() != RaceStatusEnum.PUBLISHED) {
            throw new InvalidRaceStateException("Race invalid for status: " + race.getStatus());
        }
    }

    private void validateDate(Race race) {
        if (!race.getRaceDate().isAfter(LocalDateTime.now())) {
            throw new InvalidRaceDateException("Race is not valid for date passed");
        }
    }

    private boolean isPaymentDeadlineExpired(Registration registration) {
        return registration != null && registration.getPaymentDeadline().isBefore(Instant.now());
    }

    private RegistrationResponseDto transformEntityToDto(Registration registration) {
        return new RegistrationResponseDto(registration.getId(), registration.getStatus(),
                registration.getBibNumber(), registration.getUser().getName(), registration.getRace().getName(),
                registration.getPaymentDeadline());
    }

    private RegistrationCreatedResponseDto transformEntityToCreatedDto(Registration registration,
                                                                       PaymentCreateResponseDto paymentCreateDto) {
        return new RegistrationCreatedResponseDto(registration.getId(), registration.getStatus(),
                registration.getBibNumber(), registration.getUser().getName(), registration.getRace().getName(),
                registration.getPaymentDeadline(), paymentCreateDto);
    }

    private RegistrationAdminResponseDto transformEntityAdminToDto(Registration registration) {
        MasKilometrosUser user = registration.getUser();
        UserAdminResponseDto userAdminResponseDto = new UserAdminResponseDto(user.getId(), user.getName(),
                user.getEmail(), user.getMobileNumber());

        return new RegistrationAdminResponseDto(registration.getId(), registration.getStatus(),
                registration.getBibNumber(), userAdminResponseDto, registration.getCancellationDate());
    }
}

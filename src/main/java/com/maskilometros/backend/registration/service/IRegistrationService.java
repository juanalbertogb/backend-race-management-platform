package com.maskilometros.backend.registration.service;

import com.maskilometros.backend.dto.RegistrationAdminResponseDto;
import com.maskilometros.backend.dto.RegistrationCreatedResponseDto;
import com.maskilometros.backend.dto.RegistrationResponseDto;
import com.maskilometros.backend.dto.RegistrationStatusEnum;

import java.util.List;

public interface IRegistrationService {

    RegistrationCreatedResponseDto registerToRace(Long raceId, String email);

    RegistrationResponseDto cancelRegistration(Long registrationId, String email);

    List<RegistrationAdminResponseDto> getRegistrationsAdmin(Long raceId, RegistrationStatusEnum status);

    List<RegistrationResponseDto> searchMyRegistrations(String email);

    void expirePendingRegistrations();
}

package com.maskilometros.backend.registration.controller;

import com.maskilometros.backend.dto.RegistrationCreatedResponseDto;
import com.maskilometros.backend.dto.RegistrationResponseDto;
import com.maskilometros.backend.registration.service.IRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final IRegistrationService registrationService;

    @GetMapping
    public ResponseEntity<List<RegistrationResponseDto>> searchMyRegistrations(Authentication authentication){

        String email = authentication.getName();
        List<RegistrationResponseDto> registrationResponseDtos = registrationService.searchMyRegistrations(email);

        return ResponseEntity.ok(registrationResponseDtos);
    }

    @PostMapping("/{raceId}/register")
    public ResponseEntity<RegistrationCreatedResponseDto> registerToRace(@PathVariable Long raceId, Authentication authentication){

        String email = authentication.getName();
        RegistrationCreatedResponseDto registrationResponseDto = registrationService.registerToRace(raceId, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(registrationResponseDto);
    }

    @PatchMapping("/{registrationId}/cancel")
    public ResponseEntity<RegistrationResponseDto> cancelRegistration(@PathVariable Long registrationId,
                                                                      Authentication authentication){
        String email = authentication.getName();
        RegistrationResponseDto registrationResponseDto = registrationService.cancelRegistration(registrationId, email);

        return ResponseEntity.status(HttpStatus.OK).body(registrationResponseDto);
    }


}

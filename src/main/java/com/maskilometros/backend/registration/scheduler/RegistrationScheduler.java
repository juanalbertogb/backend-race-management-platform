package com.maskilometros.backend.registration.scheduler;

import com.maskilometros.backend.registration.service.IRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationScheduler {

    private final IRegistrationService registrationService;

    @Scheduled(fixedRate = 120000)
    public void expirePendingRegistrations(){
        registrationService.expirePendingRegistrations();
    }

}

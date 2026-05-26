package com.maskilometros.backend.race.controller;

import com.maskilometros.backend.dto.*;
import com.maskilometros.backend.entity.Race;
import com.maskilometros.backend.race.service.IRaceService;
import com.maskilometros.backend.registration.service.IRegistrationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/races")
public class RaceController {

    private final IRaceService raceService;
    private final IRegistrationService registrationService;

    @GetMapping
    public ResponseEntity<List<RaceResponseDto>> getRaces(@RequestParam(required = false) RaceStatusEnum status,
                                                          @RequestParam(required = false) String location,
                                                          @RequestParam(required = false) LocalDateTime startDate,
                                                          @RequestParam(required = false) LocalDateTime endDate){
        List<RaceResponseDto> response = raceService.getRaces(status, location, startDate, endDate);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaceResponseDto> getRaceById(@PathVariable @Positive Long id){
        RaceResponseDto response = raceService.getRaceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RaceResponseDto>> getAvailableRaces(){
        List<RaceResponseDto> response = raceService.getAvailableRaces();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{raceId}/registration")
    public ResponseEntity<List<RegistrationAdminResponseDto>> getRegistrationsAdmin(@PathVariable Long raceId,
                                                                          @RequestParam(required = false) RegistrationStatusEnum status){
        List<RegistrationAdminResponseDto> registrationResponseDtos = registrationService.getRegistrationsAdmin(raceId, status);

        return ResponseEntity.ok(registrationResponseDtos);
    }

    @PostMapping
    public ResponseEntity<RaceResponseDto> createRace(@RequestBody @Valid RaceRequestDto raceRequestDto){

        RaceResponseDto raceCreated = raceService.createRace(raceRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(raceCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RaceResponseDto> updateRace(@PathVariable @Positive Long id, @RequestBody RaceRequestDto raceRequestDto){
        RaceResponseDto raceUpdated = raceService.updateRace(id, raceRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(raceUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRace(@PathVariable @Positive Long id){
        raceService.deleteRace(id);
        return ResponseEntity.status(HttpStatus.OK).body("Race deleted successfully");
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<RaceResponseDto> publishRace(@PathVariable @Positive Long id){
        RaceResponseDto racePublished = raceService.publishRace(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(racePublished);
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<RaceResponseDto> closeRace(@PathVariable @Positive Long id){
        RaceResponseDto racePublished = raceService.closeRace(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(racePublished);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<RaceResponseDto> cancelRace(@PathVariable @Positive Long id){
        RaceResponseDto racePublished = raceService.cancellRace(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(racePublished);
    }
}

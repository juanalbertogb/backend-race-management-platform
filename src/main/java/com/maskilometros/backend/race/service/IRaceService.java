package com.maskilometros.backend.race.service;

import com.maskilometros.backend.dto.RaceRequestDto;
import com.maskilometros.backend.dto.RaceResponseDto;
import com.maskilometros.backend.dto.RaceStatusEnum;
import com.maskilometros.backend.entity.Race;

import java.time.LocalDateTime;
import java.util.List;


public interface IRaceService {

    List<RaceResponseDto> getRaces(RaceStatusEnum status, String location, LocalDateTime startDate, LocalDateTime endDate);

    RaceResponseDto getRaceById(Long id);

    List<RaceResponseDto> getAvailableRaces();

    RaceResponseDto createRace(RaceRequestDto race);

    RaceResponseDto updateRace(Long id, RaceRequestDto raceRequestDto);

    void deleteRace(Long id);

    RaceResponseDto publishRace(Long id);

    RaceResponseDto closeRace(Long id);

    RaceResponseDto cancellRace(Long id);
}

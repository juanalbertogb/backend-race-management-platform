package com.maskilometros.backend.race.service.impl;


import com.maskilometros.backend.dto.RaceRequestDto;
import com.maskilometros.backend.dto.RaceResponseDto;
import com.maskilometros.backend.dto.RaceStatusEnum;
import com.maskilometros.backend.dto.RegistrationStatusEnum;
import com.maskilometros.backend.entity.Race;
import com.maskilometros.backend.exception.InvalidRaceDateException;
import com.maskilometros.backend.exception.InvalidRaceStateException;
import com.maskilometros.backend.exception.ResourceNotFoundException;
import com.maskilometros.backend.race.service.IRaceService;
import com.maskilometros.backend.repository.RaceRepository;
import com.maskilometros.backend.repository.RegistrationRepository;
import com.maskilometros.backend.specification.RaceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RaceServiceImpl implements IRaceService {

    private final RaceRepository raceRepository;
    private final RegistrationRepository registrationRepository;

    @Override
    public List<RaceResponseDto> getRaces(RaceStatusEnum status, String location,
                                          LocalDateTime startDate, LocalDateTime endDate) {
        Specification<Race> spec = Specification.where((root, query, cb) -> null);

        if(status != null){
            spec = spec.and(RaceSpecification.hasStatus(status));
        }

        if (location != null && !location.isEmpty()){
            spec = spec.and(RaceSpecification.hasLocation(location));
        }

        if ( startDate != null){
            spec = spec.and(RaceSpecification.hasDateAfter(startDate));
        }

        if (endDate != null){
            spec = spec.and(RaceSpecification.hasDateBefore(endDate));
        }
        List<Race> races = raceRepository.findAll(spec);

        Map<Long, Long> mapperIdsCounts = mapperCountRegistrations(races);


        return races.stream().map(r -> transformRaceToDto(r, mapperIdsCounts)).collect(Collectors.toList());
    }

    @Override
    public RaceResponseDto getRaceById(Long id) {
        Race race = searchRaceById(id);
        Map<Long, Long> mapperIdsCounts = mapperCountRegistrations(List.of(race));
        return transformRaceToDto(race,mapperIdsCounts);
    }

    @Override
    public List<RaceResponseDto> getAvailableRaces() {
        List<Race> list = raceRepository.findByStatusAndRaceDateAfterOrderByRaceDateAsc(RaceStatusEnum.PUBLISHED, LocalDateTime.now());
        return list.stream().map(this::transformRaceToDto).collect(Collectors.toList());
    }


    @Transactional
    @Override
    public RaceResponseDto createRace(RaceRequestDto raceRequestDto) {
        Race race = mapToEntity(raceRequestDto);
        Race savedRace = raceRepository.save(race);
        return transformRaceToDto(savedRace);
    }

    @Override
    @Transactional
    public RaceResponseDto updateRace(Long id, RaceRequestDto raceRequestDto) {
        Race race = searchRaceById(id);

        if(!race.getStatus().equals(RaceStatusEnum.DRAFT)){
            throw new InvalidRaceStateException("Race with id: "+id+" cannot be updated from status "+race.getStatus());
        }
        race.setName(raceRequestDto.name());
        race.setDescription(raceRequestDto.description());
        race.setLocation(raceRequestDto.location());
        race.setRaceDate(raceRequestDto.raceDate());
        race.setPrice(raceRequestDto.price());
        race.setMaxParticipants(raceRequestDto.maxParticipants());
        return transformRaceToDto(race);
    }

    @Override
    @Transactional
    public void deleteRace(Long id) {
        Race race = searchRaceById(id);

        if(!race.getStatus().equals(RaceStatusEnum.DRAFT)){
            throw new InvalidRaceStateException("Race with id: "+id+" cannot be deleted from status "+race.getStatus());
        }

        raceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RaceResponseDto publishRace(Long id) {
        Race race = searchRaceById(id);


        if(race.getStatus().equals(RaceStatusEnum.CLOSED) || race.getStatus().equals(RaceStatusEnum.CANCELLED)){
            throw new InvalidRaceStateException("Race with id: "+id+" cannot be published from status "+race.getStatus());
        }

        if(race.getRaceDate().isBefore(LocalDateTime.now())){
            throw new InvalidRaceDateException("Race with id:"+id+" cannot be published for date passed");
        }

        if(race.getStatus().equals(RaceStatusEnum.PUBLISHED)){
            return transformRaceToDto(race);
        }

        race.setStatus(RaceStatusEnum.PUBLISHED);
        return transformRaceToDto(race);
    }

    @Override
    @Transactional
    public RaceResponseDto closeRace(Long id) {
        Race race = searchRaceById(id);

        if(race.getStatus().equals(RaceStatusEnum.DRAFT) || race.getStatus().equals(RaceStatusEnum.CANCELLED)){
            throw new InvalidRaceStateException("Race with id: "+id+" cannot be closed from status "+race.getStatus());
        }
        if(race.getStatus().equals(RaceStatusEnum.CLOSED)){
            return transformRaceToDto(race);
        }

        race.setStatus(RaceStatusEnum.CLOSED);
        return transformRaceToDto(race);
    }

    @Override
    @Transactional
    public RaceResponseDto cancellRace(Long id) {
        Race race = searchRaceById(id);

        if(race.getStatus().equals(RaceStatusEnum.DRAFT) || race.getStatus().equals(RaceStatusEnum.CLOSED)){
            throw new InvalidRaceStateException("Race with id: "+id+" cannot be cancelled from status "+race.getStatus());
        }
        if(race.getStatus().equals(RaceStatusEnum.CANCELLED)){
            return transformRaceToDto(race);
        }

        race.setStatus(RaceStatusEnum.CANCELLED);
        return transformRaceToDto(race);
    }

    private Race searchRaceById(Long id){
       return raceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + id));
    }

    private RaceResponseDto transformRaceToDto(Race race, Map<Long, Long> mapperIdsCounts){
        long registers = mapperIdsCounts.getOrDefault(race.getId(), 0L);

        boolean soldOut= registers >= race.getMaxParticipants();

        Integer availableSlots = race.getMaxParticipants() - (int) registers;

        return new RaceResponseDto(race.getId(), race.getName(), race.getDescription(),
                race.getLocation(), race.getRaceDate(), race.getPrice(), race.getStatus(), race.getCreatedAt(),
                race.getMaxParticipants(),availableSlots,soldOut);
    }

    private RaceResponseDto transformRaceToDto(Race race){

        return new RaceResponseDto(race.getId(), race.getName(), race.getDescription(),
                race.getLocation(), race.getRaceDate(), race.getPrice(), race.getStatus(), race.getCreatedAt(),
                race.getMaxParticipants(),race.getMaxParticipants(),false);
    }

    private Map<Long, Long> mapperCountRegistrations(List<Race> races){
        List<Long> raceLong = new LinkedList<>();
        races.forEach(r -> raceLong.add(r.getId()));

        List<Object[]> racesCount = registrationRepository.countByRaceIdInAndStatusIdIn(raceLong,
                List.of(RegistrationStatusEnum.PENDING_PAYMENT, RegistrationStatusEnum.PAID));
        Map<Long, Long> raceMap = new HashMap<>();

        for(Object[] r:racesCount){
            Long raceId = (Long) r[0];
            Long count = (Long) r[1];
            raceMap.put(raceId,count);
        }

        return raceMap;
    }

    private Race mapToEntity(RaceRequestDto raceRequestDto){
        Race race = new Race();
            race.setName(raceRequestDto.name());
            race.setDescription(raceRequestDto.description());
            race.setLocation(raceRequestDto.location());
            race.setRaceDate(raceRequestDto.raceDate());
            race.setPrice(raceRequestDto.price());
            race.setMaxParticipants(raceRequestDto.maxParticipants());
        return race;
    }
}

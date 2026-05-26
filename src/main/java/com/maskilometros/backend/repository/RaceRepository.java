package com.maskilometros.backend.repository;

import com.maskilometros.backend.dto.RaceStatusEnum;
import com.maskilometros.backend.dto.RegistrationStatusEnum;
import com.maskilometros.backend.entity.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long>, JpaSpecificationExecutor<Race> {

    List<Race> findByStatusAndRaceDateAfterOrderByRaceDateAsc(RaceStatusEnum status, LocalDateTime dateAfter);

}

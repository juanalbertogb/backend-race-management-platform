package com.maskilometros.backend.repository;

import com.maskilometros.backend.dto.RegistrationStatusEnum;
import com.maskilometros.backend.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long>, JpaSpecificationExecutor<Registration> {

    Optional<Registration> fetchUserWithRaceById(@Param("userId") Long userId);

    Optional<Registration> findByUserIdAndRaceId(Long userId, Long raceId);

    List<Registration> fetchAllUsersByUserEmail(@Param("email") String email);

    List<Object[]> countByRaceIdInAndStatusIdIn(@Param("raceIds") List<Long> raceIds,
                                                @Param("status") List<RegistrationStatusEnum> status);

    long countByRaceIdAndStatusIdIn(@Param("raceId") Long raceId, @Param("status") List<RegistrationStatusEnum> status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int cleanByStatusAndPaymentDeadline(@Param("cancelled") RegistrationStatusEnum cancelled,
                                        @Param("cancellationDate") LocalDateTime cancellationDate,
                                        @Param("status") RegistrationStatusEnum status,
                                        @Param("now") Instant now);

    Integer findMaxBibNumberByRaceId(@Param("raceId") Long raceId);
}

package com.maskilometros.backend.entity;

import com.maskilometros.backend.dto.RegistrationStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jdk.jfr.Name;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "registrations")
@NamedQueries({
        @NamedQuery(name = "Registration.fetchUserWithRaceById", query =
                "SELECT DISTINCT r FROM Registration r JOIN FETCH r.race ra where r.id= :userId"),

        @NamedQuery(name = "Registration.fetchAllUsersByUserEmail", query =
                "SELECT DISTINCT r FROM Registration r JOIN FETCH r.race ra JOIN FETCH r.user us where r.user.email= :email"),

        @NamedQuery(name = "Registration.countByRaceIdAndStatusIdIn" ,query =
                "SELECT COUNT(r) FROM Registration r WHERE r.race.id = :raceId AND r.status IN :status"),

        @NamedQuery(name = "Registration.countByRaceIdInAndStatusIdIn" ,query =
                "SELECT r.race.id, COUNT(*) FROM Registration r WHERE r.race.id IN :raceIds AND r.status IN :status GROUP BY race.id"),

        @NamedQuery(name = "Registration.cleanByStatusAndPaymentDeadline" , query =
                "UPDATE Registration r SET r.status=:cancelled, r.cancellationDate= :cancellationDate, r.paymentDeadline=null "+
                        "WHERE r.status = :status AND r.paymentDeadline < :now"),

        @NamedQuery(name = "Registration.findMaxBibNumberByRaceId", query =
                "SELECT MAX(r.bibNumber) FROM Registration r where r.race.id= :raceId")

})
public class Registration extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RegistrationStatusEnum status = RegistrationStatusEnum.PENDING_PAYMENT;

    @Size(max = 20)
    @Column(name = "bib_number", length = 20)
    private String bibNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private MasKilometrosUser user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @Column(name = "cancellation_date")
    private LocalDateTime cancellationDate;

    @Column(name = "payment_deadline")
    private Instant paymentDeadline;
}
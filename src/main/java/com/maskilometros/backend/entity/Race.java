package com.maskilometros.backend.entity;

import com.maskilometros.backend.dto.RaceStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "RACES")
public class Race extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @NotNull
    @Lob
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Size(max = 255)
    @NotNull
    @Column(name = "LOCATION", nullable = false)
    private String location;

    @NotNull
    @Column(name = "RACE_DATE", nullable = false)
    @Future
    private LocalDateTime raceDate;

    @NotNull
    @Column(name = "PRICE", nullable = false)
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private RaceStatusEnum status=RaceStatusEnum.DRAFT;

    @NotNull
    @Column(name = "MAX_PARTICIPANTS", nullable = false)
    @Positive
    private Integer maxParticipants;

    @OneToMany(mappedBy = "race")
    private List<Registration> registrations;

}

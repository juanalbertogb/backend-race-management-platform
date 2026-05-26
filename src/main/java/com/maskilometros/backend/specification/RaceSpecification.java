package com.maskilometros.backend.specification;

import com.maskilometros.backend.dto.RaceStatusEnum;
import com.maskilometros.backend.entity.Race;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class RaceSpecification {

    public static Specification<Race> hasStatus(RaceStatusEnum status){
        return ((root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("status"), status));
    }

    public static Specification<Race> hasLocation(String location){
        return ((root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("location"), location));
    }

    public static Specification<Race> hasDateAfter(LocalDateTime startDate){
        return ((root, query, criteriaBuilder)
                -> criteriaBuilder.greaterThan(root.get("raceDate"), startDate));
    }

    public static Specification<Race> hasDateBefore(LocalDateTime endDate){
        return ((root, query, criteriaBuilder)
                -> criteriaBuilder.lessThan(root.get("raceDate"), endDate));
    }

}

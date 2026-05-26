package com.maskilometros.backend.specification;

import com.maskilometros.backend.dto.RegistrationStatusEnum;
import com.maskilometros.backend.entity.Registration;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class RegistrationSpecification {

    public static Specification<Registration> hasId(Long id){
        return ((root, query, cb)
                -> cb.equal(root.get("race").get("id"), id));
    }

    public static Specification<Registration> hasStatus(RegistrationStatusEnum status){
        return ((root, query, cb)
                -> cb.equal(root.get("status"), status));
    }

    public static Specification<Registration> joinFetchUser(){
        return (root, query, cb) -> {
            root.fetch("user", JoinType.LEFT);
            query.distinct(true);
                    return null;
        };
    }
}

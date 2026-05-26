package com.maskilometros.backend.repository;

import com.maskilometros.backend.entity.MasKilometrosUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasKilometrosUserRepository extends JpaRepository<MasKilometrosUser,Long> {

    boolean existsByEmail(String email);

    Optional<MasKilometrosUser> fetchUserWithRoleByEmail(@Param("email") String email);
}

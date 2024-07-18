package com.kimhab.auth.repo;

import com.kimhab.auth.model.ERole;
import com.kimhab.auth.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(ERole name);

    boolean existsByName(ERole name);
}


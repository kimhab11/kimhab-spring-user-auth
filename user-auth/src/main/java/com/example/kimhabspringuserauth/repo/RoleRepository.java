package com.example.kimhabspringuserauth.repo;

import com.example.kimhabspringuserauth.model.ERole;
import com.example.kimhabspringuserauth.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(ERole name);

    boolean existsByName(ERole name);
}


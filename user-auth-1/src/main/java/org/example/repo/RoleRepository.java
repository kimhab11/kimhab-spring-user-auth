package org.example.repo;

import org.example.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByNameContainingIgnoreCase(String roleName);
}

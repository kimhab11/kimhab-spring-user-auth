package org.example.model.mapper;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.RoleEntity;
import org.example.model.entity.UserEntity;
import org.example.model.req.RoleReq;
import org.example.model.req.UserRequest;
import org.example.model.res.UserRes;
import org.example.repo.RoleRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
// @RequiredArgsConstructor
public record UserMapper(RoleRepository roleRepository) {
    public UserRes entityToRes(UserEntity userEntity) {
        UserRes userRes = new UserRes();
        if (userEntity != null) {
            userRes.setId(userEntity.getId() != null ? userEntity.getId() : 0); // Assuming ID is of type Integer or Long
            userRes.setUsername(userEntity.getUsername() != null ? userEntity.getUsername() : "");
            userRes.setCreatedAt(userEntity.getCreatedAt() != null ? userEntity.getCreatedAt().toString() : "");
            userRes.setUpdatedAt(userEntity.getUpdatedAt() != null ? userEntity.getUpdatedAt() : null);
            userRes.setFailLoginCount(userEntity.getFailLoginAttempt());
            if (userEntity.getRoles() != null) {
                String[] rolesArray = userEntity.getRoles().stream()
                        .filter(data -> data.getName() != null)
                        .map(data -> data.getName())
                        .toArray(String[]::new);
                userRes.setRole(rolesArray);
            } else {
                userRes.setRole(new String[0]); // If roles is null, set an empty array
            }
        }
        return userRes;
    }

    public UserEntity req2Entity(UserRequest userRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userRequest.getUsername());
        userEntity.setPassword(userRequest.getPassword());
        userEntity.setRoles(roleMapper(userRequest.getRoles()));
        return userEntity;
    }

    public Set<RoleEntity> roleMapper(Set<UserRequest.RoleReq> roleReqs) {
        Set<RoleEntity> roleEntities = new HashSet<>();
        for (UserRequest.RoleReq role : roleReqs){
            RoleEntity roleEntity = new RoleEntity(role.getId());
            roleEntities.add(roleEntity);
        }
        return roleEntities;
    }

    public Set<RoleEntity> roleReq2RoleEntity(Set<RoleReq> roleReqs) {
        Set<RoleEntity> roleEntities = new HashSet<>();
        for (RoleReq role : roleReqs){
            RoleEntity roleEntity = new RoleEntity(role.getId());
            roleEntities.add(roleEntity);
        }
        return roleEntities;
    }

}

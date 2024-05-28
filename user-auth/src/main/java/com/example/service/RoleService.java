package com.example.service;

import com.example.entity.RoleEntity;
import com.example.model.ERole;
import com.example.repo.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
@Slf4j
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @PostConstruct
    protected void addRole(){
        String roles[] = { "ADMIN", "USER", "MODERATOR"};
//        for (String role: roles){
//            var exist = roleRepository.existsByName(ERole.valueOf(role));
//            log.info("exist : {}", exist);
//            if (!exist){
//                RoleEntity roleEntity = new RoleEntity(ERole.valueOf(role));
//                roleRepository.save(roleEntity);
//            }
//        }
        Arrays.stream(roles)
                .map(ERole::valueOf)
                .filter(role -> !roleRepository.existsByName(role))
                .forEach(role -> {
                    RoleEntity roleEntity = new RoleEntity(role);
                    log.info("add role: {}", roles);
                    roleRepository.save(roleEntity);
                });
    }
}

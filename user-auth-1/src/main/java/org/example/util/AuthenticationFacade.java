package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.example.repo.UserRepository;
import org.example.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class AuthenticationFacade {
    @Autowired 
    private UserRepository userServiceImp;
    public UserDetails getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null && !authentication.isAuthenticated()){
            return null ;
        } else {
            return (UserDetails) authentication.getPrincipal();
        }
    }
    
    public UserEntity getUserEntity(){
        Optional<UserEntity> user = userServiceImp.findByUsername(getUser().getUsername());
        UserEntity userEntity = user.get();
        log.info("userEntity: {}", userEntity);
        return userEntity;
    }
}

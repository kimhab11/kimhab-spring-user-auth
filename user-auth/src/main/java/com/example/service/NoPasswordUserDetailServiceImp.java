package com.example.service;

import com.example.entity.UserEntity;
import com.example.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;

@Slf4j
@Component
public class NoPasswordUserDetailServiceImp implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
//        var userDetail = NoPasswordCustomUserDetails.build(userEntity);
//        log.info("NoPasswordCustomUserDetails: {}", userDetail);

//        return userDetail;

        return new User(username, "", new HashSet<>());
    }
}

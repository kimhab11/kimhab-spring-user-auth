package com.example.kimhabspringuserauth.service;

import com.example.kimhabspringuserauth.entity.UserEntity;
import com.example.kimhabspringuserauth.repo.UserRepository;
import com.example.kimhabspringuserauth.service.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        var userDetail = UserDetailsImpl.build(userEntity);
        log.info("UserDetails: {}",userDetail);

        return userDetail;
    }

}
package com.auth.service;

import com.auth.entity.UserEntity;
import com.auth.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

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
        log.info("userEntity: {}", userEntity.toString());
        var userDetail = NoPasswordCustomUserDetails.build(userEntity);
        log.info("NoPasswordCustomUserDetails: {}", userDetail);

        return userDetail;

       // return new User(userEntity.getUsername(), "", AuthorityUtils.createAuthorityList("ROLE_USER"));
    }
}

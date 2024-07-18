package org.example.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.example.config.CustomUserDetails;
import org.example.model.entity.UserEntity;
import org.example.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional // if not will error or change  @ManyToMany(fetch = FetchType.EAGER) in User Entity for Set<RoleEntity>
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);

        if (!userEntityOptional.isPresent()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        UserEntity userEntity = userEntityOptional.get();


        var simpleGrantedAuthorities = Collections.singleton(new SimpleGrantedAuthority(userEntity.getRoles().toString()));
        log.info("simpleGrantedAuthorities: {}", simpleGrantedAuthorities);

        CustomUserDetails userDetail = new CustomUserDetails(userEntity, getAuthority(userEntity));
        log.info("User: {}", userEntity);
        return userDetail;
    }

    public Set<SimpleGrantedAuthority> getAuthority(UserEntity user){
        var userRoles = user.getRoles();
        var role = userRoles.stream().map(r-> r.getName()).collect(Collectors.toList());
        log.info("role: {}", role);
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (var roleName : role) {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+roleName));
        }
        return authorities;
    }
}

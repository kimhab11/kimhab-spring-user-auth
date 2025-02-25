package com.auth.service;

import com.auth.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ToString
public class NoPasswordCustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public NoPasswordCustomUserDetails(Long id, String username, String email, String password,
                                       Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static NoPasswordCustomUserDetails build(UserEntity userEntity) {
        List<GrantedAuthority> authorities = userEntity.getRoleEntities().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role.getName().name()))
                .collect(Collectors.toList());
        log.info("authorities: {}", authorities);

        return new NoPasswordCustomUserDetails(userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                "",
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NoPasswordCustomUserDetails user = (NoPasswordCustomUserDetails) o;
        return Objects.equals(id, user.id);
    }

}

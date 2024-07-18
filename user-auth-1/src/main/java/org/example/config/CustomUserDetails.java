package org.example.config;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.example.model.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@ToString
public class CustomUserDetails implements UserDetails {
    private UserEntity userEntity;

    private Collection<? extends GrantedAuthority> grantedAuthorityList;

    public CustomUserDetails(String username, String password, boolean isEnabled, Collection<? extends GrantedAuthority> authorities) {
        this.userEntity.setUsername(username);
        this.userEntity.setIsEnabled(isEnabled);
        this.userEntity.setPassword(password);
        this.grantedAuthorityList = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
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
        return userEntity.getIsEnabled();
    }
}
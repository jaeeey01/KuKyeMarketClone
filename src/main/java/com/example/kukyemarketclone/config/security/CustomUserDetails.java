package com.example.kukyemarketclone.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {// UserDetails : spring security에서 제공하는 인터페이스 구현
    // CustomUserDetails :인증된 사용자의 정보와 권한을 담고 있음

    // 사용자의 접근을 제어하기 위해 최소한으로 필요한 userid와 권한 등급만 필드 선언
    private final String userId;
    private final Set<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override //실제로 사용하거나 사용할 수 있는, 유효한 메서드가 아니므로 호출시 예외 발생
    public String getPassword() {
        throw  new UnsupportedOperationException();
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAccountNonLocked() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEnabled() {
        throw new UnsupportedOperationException();
    }
}

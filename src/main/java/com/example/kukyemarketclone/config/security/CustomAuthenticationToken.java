package com.example.kukyemarketclone.config.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {
    /* CustomAuthenticationToken
    * CustomUserDetailsService를 이용하여 조회된 사용자의 정보 CustomUserDetails와 요청 토큰의 타입을 저장
    * Spring Security에서 제공해주는 추상 클래스 AbstractAuthenticationToken을 상속받아
    * 우리가 사용자를 인증하는데 필요한 최소한의 정보를 기억하도록 함
    * 단순히, 토큰의 타입과 CustomUserDetails, 권한 등급 정보를 가지게 됨
    * 허용되지 않는 동작은 예외를 발생
     * */

    private CustomUserDetails principal;

    public CustomAuthenticationToken(CustomUserDetails principal, Collection<? extends GrantedAuthority> authorities ){
        super(authorities);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {

        throw new UnsupportedOperationException();
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }


}

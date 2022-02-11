package com.example.kukyemarketclone.config.security.guard;

import com.example.kukyemarketclone.config.security.CustomAuthenticationToken;
import com.example.kukyemarketclone.config.security.CustomUserDetails;
import com.example.kukyemarketclone.entity.member.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthHelper {//사용자 인증 정보를 추출하기 위해 도움 주는 클래스

    /* 사용자 인증 정보는 Spring security에서 관리해주는 컨텍스트에 저장
    *  해당 정보는 , ThreadLocal을 이용하여 관리
    *  즉 같은 스레드를 공유하고 있다면, 어떤 위치에서 사용하든지 저장해둔 데이터를 공유 가능
    *  AuthHelper = ThreadLocal에 저장된 정보를 통해 요청자의 id, 인증여부, 권한 등급, 요청토큰 타입 추출 하는데 도움
    * */


    public boolean isAuthenticated(){
        return getAuthentication() instanceof CustomAuthenticationToken &&
                getAuthentication().isAuthenticated();
    }

    public Long extactMemberId(){
        return Long.valueOf(getUserDetails().getUserId());
    }

    public Set<RoleType> extractMemberRoles(){
        return getUserDetails().getAuthorities()
                .stream()
                .map(authhority -> authhority.getAuthority())
                .map(strAuth -> RoleType.valueOf(strAuth))
                .collect(Collectors.toSet());
    }

    public boolean isAccessTokenType(){
        return "access".equals(((CustomAuthenticationToken) getAuthentication()).getType());
    }

    public boolean isRefreshTokenType(){
        return "refresh".equals(((CustomAuthenticationToken) getAuthentication()).getType());
    }

    private CustomUserDetails getUserDetails(){
        return (CustomUserDetails) getAuthentication().getPrincipal();
    }


    /*getAuthentication
    * 인증되지 않은 사용자여도 spring security에서 등록해준 필터에 의해 AnonymousAuthenticationToken을 발급 받기 때문에
    * getAuthentication()의 반환 값이 우리가 직접 정의한 CustomAuthenticationToken일 때만 인증 된 것으로 판별
    * */

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}

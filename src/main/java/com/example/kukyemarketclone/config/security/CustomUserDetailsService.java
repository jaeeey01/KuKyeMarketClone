package com.example.kukyemarketclone.config.security;

import com.example.kukyemarketclone.config.token.TokenHelper;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Component//security에서 관리하고 사용될 것으로 @Component 선언
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /*CustomUserDetailsService
    * spring security에서 제공해주는 UserDetailsService를 구현하는 CustomUserDetailsService는
    * 인증된 사용자의 정보를 CustomUserDetails로 반환해줌
    * CustomUserDetailsService는 스프링 컨테이너에 등록되기 때문에 다른 의존성들을 주입받을 수 있음
    * */

    private final TokenHelper accessTokenHelper;


    //변경 : DB에 접근하던 전 코드와 달리 DB에 접근 필요성 X
    //단순히 전달받은 토큰에서 필요한 정보만 추출 후 CustomUserDetails 생성
    //유효하지 않은 토큰이라면 null반환
    @Override
    public CustomUserDetails loadUserByUsername(String token) throws UsernameNotFoundException {
        return accessTokenHelper.parse(token)
                .map(this::convert)
                .orElse(null);
    }

    private CustomUserDetails convert(TokenHelper.PrivateClaims privateClaims){
        return new CustomUserDetails(
                privateClaims.getMemberId(),
                privateClaims.getRoleTypes().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
        );
    }
}

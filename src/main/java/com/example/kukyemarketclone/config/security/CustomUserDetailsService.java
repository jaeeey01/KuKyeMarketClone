package com.example.kukyemarketclone.config.security;

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


    private final MemberRepository memberRepository;


    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findWithRolesById(Long.valueOf(userId))//토큰에서 추출한 사용자의 id를 이용하여 Member 조회

                //만약 사용자를 찾지 못했다면, 권한이 없고 비어 있는 CustomUserDetails를 생성 반환
                .orElseGet(() -> new Member(null,null,null,null, List.of()));
        return new CustomUserDetails(//권한 등급을  GrantedAuthority 인터페이스 타입으로 받게 되는데 이의 간단 구현체인 SimpleGrantedAuthority 이용
                String.valueOf(member.getId()),
                member.getRoles().stream().map(memberRole -> memberRole.getRole())
                        .map(role -> role.getRoleType())
                        .map(roleType -> roleType.toString())//권한 등급은 String 타입으로 인식하기 때문에 RoleType을 String 으로 변환
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
        );
    }
}

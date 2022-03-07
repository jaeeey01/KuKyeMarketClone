package com.example.kukyemarketclone.config.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
//@Component를 등록시 자동으로 필터체인에 등록되기에 생략 : 이미 순서 제어를 위해 SecurityConfig에 직접 생성하여 필터체인에 등록 했기 때문
//이러한 이유로 필요한 의존성들은 SecurityConfig에서 받아다가 주입함
public class JwtAuthenticationFilter extends GenericFilterBean {

    /*JwtAuthenticationFilter
    * 사용자 정보를 ThreadLocal에 저장
    * 해당 필터는 UsernamePasswordAuthenticationFilter 이전에 등록 = SecurityConfig 참조
    * UsernamePasswordAuthenticationFilter는 자신이 처리할 요청이 들어오면 다음 필터를 거치지 않기 때문에
    * 그 이전에 필터 등록을 해야 정상적으로 인증 수행 가능
     * */

    private final CustomUserDetailsService userDetailsService;

    //변경 : 요청에서 토큰 추출, 토큰이 있다면 CustomUserDetails를 반환하고 Security
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
       extractToken(request).map(userDetailsService::loadUserByUsername).ifPresent(this::setAuthentication);
       chain.doFilter(request, response);
    }

    private Optional<String> extractToken(ServletRequest request){//요청으로 전달 받은 Authorization헤더에서 토큰 값 꺼내옴
        return Optional.ofNullable(((HttpServletRequest)request).getHeader("Authorization"));
    }

    private void setAuthentication(CustomUserDetails userDetails){
        SecurityContextHolder.getContext().setAuthentication(new CustomAuthenticationToken(userDetails, userDetails.getAuthorities()));
    }

}

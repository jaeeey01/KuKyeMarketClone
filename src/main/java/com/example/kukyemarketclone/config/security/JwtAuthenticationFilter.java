package com.example.kukyemarketclone.config.security;


import com.example.kukyemarketclone.config.token.TokenHelper;
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

    private final TokenHelper accessTokenHelper;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = extractToken(request);
        if(validateToken(token)){ //토큰값 유효시
            //springSecurity가 관리해주는 컨텍스트에 사용자 정보를 등록
            //SecurityContextHolder -> ContextHolder -> Autentication인터페이스의 구현체 CustomAuthenticationToken를 등록
            setAuthentication("access",token);
        }
        chain.doFilter(request, response);
    }

    private String extractToken(ServletRequest request){//요청으로 전달 받은 Authorization헤더에서 토큰 값 꺼내옴
        return ((HttpServletRequest)request).getHeader("Authorization");
    }

    private boolean validateToken(String token){
        return token != null && accessTokenHelper.validate(token);
    }

    private void setAuthentication(String type, String token){
        String userId = accessTokenHelper.extractSubject(token);
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        SecurityContextHolder.getContext().setAuthentication(new CustomAuthenticationToken(type, userDetails, userDetails.getAuthorities()));
    }

}

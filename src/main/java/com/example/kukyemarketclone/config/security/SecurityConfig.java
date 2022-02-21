package com.example.kukyemarketclone.config.security;

import com.example.kukyemarketclone.config.token.TokenHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity //Security 관련 설정과 빈 활성화
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter { // extends 하여 설정작업 수행

    //토큰을 통해 사용자 인증을 위한 JwtAuthenticationFilter에 필요한 의존성
    private final TokenHelper accessTokenHelper;

    //토큰을 통해 사용자 인증을 위한 JwtAuthenticationFilter에 필요한 의존성
    //토큰에 저장된 subject(userId)로 사용자 정보 조회 목적
    private final CustomUserDetailsService userDetailsService;

    @Override
    public void configure(WebSecurity web) throws Exception{
        // security를 무시할 url 지정
        //'/exception'으로 요청이 들어왔을 경우 Spring security를 거치지 않고
        //바로 컨트롤러로 요청이 도달
        web.ignoring().mvcMatchers("/exception/**","/swagger-ui/**","/swagger-resources/**","/v3/api-docs/**");
    }

    @Override
    protected void configure(HttpSecurity http)throws Exception{
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션 유지 안되도록 설정
                .and()
                    .authorizeRequests() //각 메소드와 URL에 따른 접근 정책 설정
                        .antMatchers(HttpMethod.POST,"/api/sign-in","/api/sign-up","/api/refresh-token").permitAll()
                        .antMatchers(HttpMethod.GET,"/image/**").permitAll()
                        .antMatchers(HttpMethod.GET,"/api/**").permitAll()

                            //access 작성 방식 : @<빈이름>.<메소드명>(<인자, #id로하면 URL에 지정한 {id}가 매핑되어서 인자로 들어감>)
                            //삭제 요청은 본인과 관리자만 수행 가능 : 검증 로직을 수행하기 위해 @memberGuard.check의 반환 결과가 true면 요청 수행
                        .antMatchers(HttpMethod.DELETE,"/api/members/{id}/**").access("@memberGuard.check(#id)")
                        .antMatchers(HttpMethod.POST,"/api/categories/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE,"/api/categories/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.POST,"/api/posts").authenticated()
                        .antMatchers(HttpMethod.DELETE,"/api/posts/{id}").authenticated()
                        .anyRequest().hasAnyRole("ADMIN")
                .and()
                    .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())//5 인증된 사용자가 권한 부족등의 사유로 접근 거부시 작동할 핸들러 지정
                .and()
                    .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())//6 인증되지 않은 사용자의 접근 거부시 작동할 핸들러 지정
                .and()
                    //토큰으로 사용자를 인증하기 위해 직접 정의한 JwtAuthenticationFilter를  UsernamePasswordAuthenticationFilter 이전 위치에 등록
                    //JwtAuthenticationFilter는 필요한 의존성인 TokenService와 CustomUserDetailsService를 주입 받음
                    .addFilterBefore(new JwtAuthenticationFilter(accessTokenHelper,userDetailsService), UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        //팩토리 메소드를 이용하면 인스턴스 생성 가능
        //비밀번호 암호화를 위한 다양한 알고리즘을 선택적으로 편리하게 사용 가능
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();

    }
}

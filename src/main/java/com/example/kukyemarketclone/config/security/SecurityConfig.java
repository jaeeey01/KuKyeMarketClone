package com.example.kukyemarketclone.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
@EnableGlobalMethodSecurity(prePostEnabled = true)//메소드 레벨에 Security 설정 활성화 -> 메소드 수행 전후에 권한검사 가능
public class SecurityConfig extends WebSecurityConfigurerAdapter { // extends 하여 설정작업 수행

    private final CustomUserDetailsService userDetailsService;

    @Override
    public void configure(WebSecurity web) throws Exception{
        // security를 무시할 url 지정
        //'/exception'으로 요청이 들어왔을 경우 Spring security를 거치지 않고
        //바로 컨트롤러로 요청이 도달
        web.ignoring().mvcMatchers("/swagger-ui/**","/swagger-resources/**","/v3/api-docs/**");
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
                        .antMatchers(HttpMethod.GET,"/image/**").permitAll()
                        .antMatchers(HttpMethod.POST,"/api/sign-in","/api/sign-up","/api/refresh-token").permitAll()
                        .antMatchers(HttpMethod.DELETE,"/api/members/{id}/**").authenticated()
                        .antMatchers(HttpMethod.POST,"/api/categories/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE,"/api/categories/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.POST,"/api/posts").authenticated()
                        .antMatchers(HttpMethod.PUT,"/api/posts/{id}").authenticated()
                        .antMatchers(HttpMethod.DELETE,"/api/posts/{id}").authenticated()
                        .antMatchers(HttpMethod.POST,"/api/comments").authenticated()
                        .antMatchers(HttpMethod.DELETE,"/api/comments/{id}").authenticated()

                        //쪽지 목록  조회는 memberId의 주입이 필요하므로 인증된 사용자만 가능
                        //쪽지 조회, 삭제는 관리자 또는 자원의 소유자가 가능
                        // 쪽지 생성은 인증된 사용자가 할 수 있음
                        .antMatchers(HttpMethod.GET,"/api/messages/sender", "/api/messages/receiver").authenticated()
                        .antMatchers(HttpMethod.GET,"/api/messages/{id}").authenticated()
                        .antMatchers(HttpMethod.POST,"/api/messages").authenticated()
                        .antMatchers(HttpMethod.DELETE,"/api/messages/sender/{id}").authenticated()
                        .antMatchers(HttpMethod.DELETE,"/api/messages/receiver/{id}").authenticated()

                        .antMatchers(HttpMethod.GET,"/api/**").permitAll()//주의 :::: 구체적인 것이 앞서 등록되야함
                        .anyRequest().hasAnyRole("ADMIN")
                .and()
                    .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())//5 인증된 사용자가 권한 부족등의 사유로 접근 거부시 작동할 핸들러 지정
                .and()
                    .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())//6 인증되지 않은 사용자의 접근 거부시 작동할 핸들러 지정
                .and()
                    //토큰으로 사용자를 인증하기 위해 직접 정의한 JwtAuthenticationFilter를  UsernamePasswordAuthenticationFilter 이전 위치에 등록
                    //JwtAuthenticationFilter는 필요한 의존성인 TokenService와 CustomUserDetailsService를 주입 받음
                    .addFilterBefore(new JwtAuthenticationFilter(userDetailsService), UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        //팩토리 메소드를 이용하면 인스턴스 생성 가능
        //비밀번호 암호화를 위한 다양한 알고리즘을 선택적으로 편리하게 사용 가능
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();

    }
}

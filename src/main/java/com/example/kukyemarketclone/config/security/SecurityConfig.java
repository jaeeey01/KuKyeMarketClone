package com.example.kukyemarketclone.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity //Security 관련 설정과 빈 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter { // extends 하여 설정작업 수행

    @Override
    public void configure(WebSecurity web) throws Exception{
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http)throws Exception{
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션 유지 안되도록 설정
                .and()
                .authorizeRequests()
                .antMatchers("**").permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        //팩토리 메소드를 이용하면 인스턴스 생성 가능
        //비밀번호 암호화를 위한 다양한 알고리즘을 선택적으로 편리하게 사용 가능
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();

    }
}

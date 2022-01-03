package com.example.kukyemarketclone.handler;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;


class JwtHandlerTest {

    JwtHandler jwtHandler = new JwtHandler();


    @Test
    void createTokenTest() {
        //given,when
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());
        String token = createToken(encodedKey,"subject",60L);

        //then
        assertThat(token).contains("Bearer ");
    }

    @Test
    void extractSubjectTest() {
        //given
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());
        String subject = "subject";
        String token = createToken(encodedKey,subject,60L);

        //when
        String extractedSubject = jwtHandler.extractSubject(encodedKey,token);

        //then
        assertThat(extractedSubject).isEqualTo(subject);
    }

    @Test
    void validateTest() {
        //given
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());
        String token = createToken(encodedKey,"subject",60L);

        //when
        boolean isValid = jwtHandler.validate("invalid",token);

        assertThat(isValid).isFalse();

    }

    @Test
    void invalidateByExpiredTokenTest(){
        //given
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());

        //토큰 생성시 만료시간이 0 이기 때문에 생성과 동시에 만료 = false
        String token = createToken(encodedKey,"subject",0L);

        //when
        boolean isValid = jwtHandler.validate(encodedKey,token);

        //then
        assertThat(isValid).isFalse();

    }

    private String createToken(String encodedKey, String subject, long maxAgeSeconds){
        return jwtHandler.createToken(encodedKey,subject,maxAgeSeconds);
    }
}
package com.example.kukyemarketclone.dto.sign;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static com.example.kukyemarketclone.factory.dto.SignInRequestFactory.*;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

public class SignInRequestValidationTest {
    //검증 작업 수행 위한 Validator 빌드
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    void validateTest(){
        //given
        SignInRequest req = createSignInRequest();

        //when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        //then
        assertThat(validate).isEmpty();//제약 조건 모두 지키고 올바르게 검증시 응답결과 비어있음
    }

    @Test
    void invalidateByNotFormattedEmailTest(){
        //given
        String invalidValue = "email";
        SignInRequest req = createSignInRequestWithEmail(invalidValue);

        //when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        //then
        assertThat(validate).isNotEmpty();  //제약조건 위반시 응답결과 비어있지 않음

        //응답 결과에서 제약 조건 위반한 객체를 꺼내서 given에서 설정 해두었던 위반된 값을 가지고 있는지 확인
        assertThat(validate.stream().map( v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);

    }

    @Test
    void invalidateByEmptyEmailTest(){
        //given
        String invalidValue = null;
        SignInRequest req = createSignInRequestWithEmail(invalidValue);

        //when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map( v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankEmailTest(){
        //given
        String invalidValue = " ";
        SignInRequest req = createSignInRequestWithEmail(invalidValue);

        //when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map( v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }
    
    @Test
    void invalidateByEmptyPasswordTest(){
        //given
        String invalidValue = null;
        SignInRequest req = createSignInRequestWithPassword(invalidValue);

        //when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);

    }

    @Test
    void invalidateByBlankPasswordTest(){
        //given
        String invalidValue = "   ";
        SignInRequest req = createSignInRequestWithPassword(invalidValue);

        //when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);

    }


}

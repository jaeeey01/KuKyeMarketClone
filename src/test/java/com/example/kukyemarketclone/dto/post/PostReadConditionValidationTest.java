package com.example.kukyemarketclone.dto.post;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.kukyemarketclone.factory.dto.PostReadConditionFactory.createPostReadCondition;
import static org.assertj.core.api.Assertions.assertThat;

public class PostReadConditionValidationTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest(){
        //given
        PostReadCondition cond = createPostReadCondition(1,1);

        //when
        Set<ConstraintViolation<PostReadCondition>> validate = validator.validate(cond);

        //then
        assertThat(validate).isEmpty();;
    }

    @Test
    void invalidateByNullPageTest(){
        //given
        Integer invalidValue = null;
        PostReadCondition cond = createPostReadCondition(invalidValue,1);

        //when
        Set<ConstraintViolation<PostReadCondition>> validate = validator.validate(cond);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map( v -> v.getInvalidValue()).collect(Collectors.toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNegativePageTest(){
        //given
        Integer invalidValue = -1;
        PostReadCondition cond = createPostReadCondition(invalidValue,1);

        //when
        Set<ConstraintViolation<PostReadCondition>> validate = validator.validate(cond);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(Collectors.toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNullSizeTest(){
        //given
        Integer invalidValue = null;
        PostReadCondition cond = createPostReadCondition(1,invalidValue);

        //when
        Set<ConstraintViolation<PostReadCondition>> validate = validator.validate(cond);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map( v -> v.getInvalidValue()).collect(Collectors.toSet())).contains(invalidValue);
    }

    @Test
    void invalidByNegativeOrZeroPageTest(){
        //given
        Integer invalidValue = 0;
        PostReadCondition cond = createPostReadCondition(1, invalidValue);

        //when
        Set<ConstraintViolation<PostReadCondition>> validate = validator.validate(cond);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map( v -> v.getInvalidValue()).collect(Collectors.toSet())).contains(invalidValue);
    }
}

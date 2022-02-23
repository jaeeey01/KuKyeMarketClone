package com.example.kukyemarketclone.dto.comment;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.kukyemarketclone.factory.dto.CommentReadConditionFactory.createCommentReadCondition;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CommentReadConditionTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest(){
        //given
        CommentReadCondition cond =createCommentReadCondition();

        //when
        Set<ConstraintViolation<CommentReadCondition>> validate = validator.validate(cond);

        //then
        assertThat(validate).isEmpty();
    }

    @Test
    void invalidateByNegativePostIdTest(){
        //given
        Long invalidValue = -1L;
        CommentReadCondition cond = createCommentReadCondition(invalidValue);

        //when
        Set<ConstraintViolation<CommentReadCondition>> validate = validator.validate(cond);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map( v -> v.getInvalidValue()).collect(Collectors.toSet())).contains(invalidValue);
    }

}
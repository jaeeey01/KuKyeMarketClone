package com.example.kukyemarketclone.handler;

import com.example.kukyemarketclone.dto.response.Failure;
import com.example.kukyemarketclone.dto.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import static com.example.kukyemarketclone.exception.ExceptionType.BIND_EXCEPTION;
import static com.example.kukyemarketclone.exception.ExceptionType.EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;

class ResponseHandlerTest {
    ResponseHandler responseHandler;

    @BeforeEach
    void beforeEach(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/exception");
        responseHandler = new ResponseHandler(messageSource);
    }

    @Test
    void getFailureResponseNoArgsTest() {
        //given, when
        Response failureResponse = responseHandler.getFailureResponse(EXCEPTION);

        //then
        assertThat(failureResponse.getCode()).isEqualTo(-1000);
        assertThat(((Failure) failureResponse.getResult()).getMsg()).isEqualTo("오류가 발생하였습니다");
    }

    @Test
    void testGetFailureResponseWithArgsTest() {
        //given, when
        Response failureResponse = responseHandler.getFailureResponse(BIND_EXCEPTION,"my args");

        //then
        assertThat(failureResponse.getCode()).isEqualTo(-1003);
        assertThat(((Failure)failureResponse.getResult()).getMsg()).isEqualTo("my args");
    }
}
package com.example.kukyemarketclone.controller.sign;

import com.example.kukyemarketclone.advice.ExceptionAdvice;
import com.example.kukyemarketclone.dto.sign.SignInRequest;
import com.example.kukyemarketclone.dto.sign.SignUpRequest;
import com.example.kukyemarketclone.exception.*;
import com.example.kukyemarketclone.factory.dto.SignInRequestFactory;
import com.example.kukyemarketclone.factory.dto.SignUpRequestFactory;
import com.example.kukyemarketclone.handler.ResponseHandler;
import com.example.kukyemarketclone.service.sign.SignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SignControllerAdviceTest {

    @InjectMocks SignController signController;

    @Mock
    SignService signService;
    @Mock
    ResponseHandler responseHandler;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/exception");
        mockMvc = MockMvcBuilders.standaloneSetup(signController).setControllerAdvice(new ExceptionAdvice(responseHandler)).build();
    }

    @Test
    void signInLoginFailureExceptionTest()throws Exception {
        //given
        SignInRequest req = SignInRequestFactory.createSignInRequest("email@email.com","123456a!");
        given(signService.signIn(any())).willThrow(LoginFailureException.class);

        //when, then
        mockMvc.perform(
                post("/api/sign-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void signInMethodArgumentNotValidExceptionTest()throws Exception{
        //given
        SignInRequest req = SignInRequestFactory.createSignInRequest("email","1234567");

        //when,then
        mockMvc.perform(
                post("/api/sign-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void signUpMemberEmailAlreadyExistsExceptionTest()throws Exception {
        //given
        SignUpRequest req = SignUpRequestFactory.createSignUpRequest();
        doThrow(MemberEmailAlreadyExistsException.class).when(signService).signUp(any());

        //when, then
        mockMvc.perform(
                post("/api/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void signUpMemberNicknameAlreadyExistsExceptionTest()throws Exception{//void 반환형 일 경우 검증
        //given
        SignUpRequest req = SignUpRequestFactory.createSignUpRequest();
        doThrow(MemberNicknameAlreadyExistsException.class).when(signService).signUp(any());

        //when, then
        mockMvc.perform(
                post("/api/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void signUpRoleNotFountExceptionTest () throws Exception{
        //given
        SignUpRequest req = SignUpRequestFactory.createSignUpRequest();
        //발생할 예외클래스 명시, when을 이용하여 예외가 발생할 객체의 메소드 지정
        doThrow(RoleNotFoundException.class).when(signService).signUp(any());

        //when, then
        mockMvc.perform(
                post("/api/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void signUpMethodArgumentNotValidExceptionTest() throws Exception{// 제약조건 검증
        //given
        SignUpRequest req = SignUpRequestFactory.createSignUpRequest("","","","");

        //when, then
        mockMvc.perform(
                post("/api/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshTokenAuthenticationEntryPointException() throws Exception{
        //given
        given(signService.refreshToken(anyString())).willThrow(RefreshTokenFailureException.class);

        //when, then
        mockMvc.perform(
                post("/api/refresh-token")
                        .header("Authorization","refreshToken"))
                .andExpect(status().isBadRequest());


    }

    @Test
    void refreshTokenMissingRequestHeaderException() throws Exception{
        //given, when, then
        mockMvc.perform(
                post("/api/refresh-token"))
                .andExpect(status().isBadRequest());
    }


}

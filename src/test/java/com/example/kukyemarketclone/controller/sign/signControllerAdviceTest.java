package com.example.kukyemarketclone.controller.sign;

import com.example.kukyemarketclone.advice.ExceptionAdvice;
import com.example.kukyemarketclone.dto.sign.SignInRequest;
import com.example.kukyemarketclone.dto.sign.SignUpRequest;
import com.example.kukyemarketclone.exception.LoginFailureException;
import com.example.kukyemarketclone.exception.MemberEmailAlreadyExistsException;
import com.example.kukyemarketclone.exception.MemberNicknameAlreadyExistsException;
import com.example.kukyemarketclone.exception.RoleNotFoundException;
import com.example.kukyemarketclone.service.sign.SignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class signControllerAdviceTest {

    @InjectMocks SignController signController;

    @Mock
    SignService signService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(signController).setControllerAdvice(new ExceptionAdvice()).build();
    }

    @Test
    void signInLoginFailureExceptionTest()throws Exception {
        //given
        SignInRequest req = new SignInRequest("email@email.com","123456a!");
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
        SignInRequest req = new SignInRequest("email","1234567");

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
        SignUpRequest req = new SignUpRequest("email@email.com","123456a!","username","nickname");
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
        SignUpRequest req = new SignUpRequest("email@email.com","123456a!","username","nickname");
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
        SignUpRequest req = new SignUpRequest("email@email.com","123456a!","username","nickname");
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
        SignUpRequest req = new SignUpRequest("","","","");

        //when, then
        mockMvc.perform(
                post("/api/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}

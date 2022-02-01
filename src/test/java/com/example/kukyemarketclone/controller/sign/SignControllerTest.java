package com.example.kukyemarketclone.controller.sign;

import com.example.kukyemarketclone.dto.sign.SignInRequest;
import com.example.kukyemarketclone.dto.sign.SignInResponse;
import com.example.kukyemarketclone.dto.sign.SignUpRequest;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SignControllerTest {

    @InjectMocks SignController signController;
    @Mock
    SignService signService;

    MockMvc mockMvc;

    // 객체 -> JSON 문자열로 변환
    //objectMapper.writeValueAsString(req)
    ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(signController).build();
    }


    @Test
    void signUpTest() throws Exception {
        //given
        SignUpRequest req = new SignUpRequest("email@email.com","123456a!","username","nickname");
        
        //when,then
        mockMvc.perform(
                post("/api/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(signService).signUp(req);
    }

    @Test
    void signInTest() throws Exception {
        //given
        SignInRequest req = new SignInRequest("email@email.com","123456a!");
        given(signService.signIn(req)).willReturn(new SignInResponse("access","refresh"));

        //when, then
        mockMvc.perform(
                post("/api/sign-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.accessToken").value("access")) //응답 JSON에 포함되어 있는지 확인
                .andExpect(jsonPath("$.result.data.refreshToken").value("refresh"));

        verify(signService).signIn(req);
    }

    @Test
    void ignoreNullValueInJsonResponseTest() throws Exception {//응답 결과로 반환되는 JSON문자열 올바르게 제거 되는지 확인
        //given
        SignUpRequest req = new SignUpRequest("email@email.com","123456a!","username","nickname");

        //when, then
        mockMvc.perform(
                post("/api/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").doesNotExist());

    }
}
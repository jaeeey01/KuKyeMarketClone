package com.example.kukyemarketclone.controller.member;

import com.example.kukyemarketclone.dto.sign.SignInRequest;
import com.example.kukyemarketclone.dto.sign.SignInResponse;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.init.TestInitDB;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.service.sign.SignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;


import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)//1
@AutoConfigureMockMvc//2
@ActiveProfiles(value = "test")//3
@Transactional//4
class MemberControllerIntegrationTest {

    @Autowired
    WebApplicationContext context; //5 MockMvc를 빌드하기 위해 WebApplicationContext 주입
    @Autowired
    MockMvc mockMvc;//6 API 요청 보내고 테스트 하기위해 주입
    @Autowired
    TestInitDB initDB;//7 통합 테스트에서 사용될 데이터 초기화용 빈, 테스트 데이터 삽입, DB 초기화
    @Autowired
    SignService signService;
    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void BeforeEach(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();//MockMvcBuilders를 이용해서 MockMvc 초기화, SpringSecurity 활성화를 위해 호출 : apply()
        initDB.InitDB();
    }

    @Test
    void readTest() throws Exception{
        //given
        Member member = memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);

        //when
        mockMvc.perform(
                get("/api/members/{id}",member.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTest() throws Exception{
        //given
        Member member = memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);
        SignInResponse signRes = signService.signIn(new SignInRequest(initDB.getMember1Email(),initDB.getPassword()));

        //when, then
        mockMvc.perform(
                delete("/api/members/{id}",member.getId()).header("Authorization",signRes.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteByAdminTest() throws Exception{
        //given
        Member member = memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);
        SignInResponse adminSignInRes = signService.signIn(new SignInRequest(initDB.getAdminEmail(),initDB.getPassword()));

        //when, then
        mockMvc.perform(
                delete("/api/members/{id}",member.getId()).header("Authorization",adminSignInRes.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUnauthorizedByNoneTokenTest() throws Exception{
        //given
        Member member = memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);

        //when, then
        mockMvc.perform(
                delete("/api/members/{id}",member.getId())) //엑세스토큰이 헤더에 포함되어있지 않음 = CustomAuthenticationEntryPoint 작동
                .andExpect(status().is3xxRedirection())//3xx 상태코드 응답
                .andExpect(redirectedUrl("/exception/entry-point"));

    }

    @Test
    void deleteAccessDeniedByNotResourceOwnerTest() throws Exception{
        //given
        Member member = memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);
        SignInResponse signInRes = signService.signIn(new SignInRequest(initDB.getMember2Email(),initDB.getPassword()));

        //when,then
        mockMvc.perform( //남의 자원 접근권한 없음 = CustomAccessDeniedHandler 작동
                delete("/api/members/{id}",member.getId()).header("Authorization",signInRes.getAccessToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));

    }

    @Test
    void deleteAccessDeniedByRefreshTokenTest() throws Exception{
        //given
        Member member= memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);
        SignInResponse signInRes = signService.signIn(new SignInRequest(initDB.getMember1Email(),initDB.getPassword()));

        //when, then
        mockMvc.perform(    // 정상 사용자, Refresh 토큰으로 접근시 제한 = CustomAccessDeniedHandler 작동
                delete("/api/members/{id}",member.getId()).header("Authorization",signInRes.getRefreshToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }
}

/*1. @SpringBootTest
* Spring Security를 이용해서 검증하려면 여러가지 빈들이 등록되고 협력해야함
* 에 1 : 삭제 요청 수행 검증 -> 토큰 유효성 검증하면서 사용자 정보 DB조회
* 예 2 : 접근 제어정책 검증시에 MemberGuard, AuthHelper등 여러 객체 협력
* = 일일이 구분 짓기엔 어려움이 있음으로 @SpringBootTest를 선언

* 2. 기본 웹 관련설정 - webEnvironment.MOCK
* 내장 톰캣으로 실제 서버를 띄우는 것이 아니라 가짜로 웹환경을 만들어서 테스트 수행
* MockMvc는 자동으로 스프링 빈에 등록되지 않음 - 이를 주입하기 위해 @AutoConfigureMockMvc 선언
* 내장 톰캣으로 서버 띄우고 싶을 경우(권장) - @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
* */

/*3. @ActiveProfiles(value = "test")
* 스프링 부트 앱 실행시 설정파일을 통해 기본적으로 profiles = local
* 이 profiles로 수행시 InitDB가 빈으로 등록되면서 테스트와 무관한 데이터들이 초기화, 하지만 통합테스트를 위한 데이터 별도 삽입 할 예정
*  @ActiveProfiles("test")로 설정하여 충돌 방지
* */

/*4. @Transactional
* 테스트 데이터를 초기화 하거나 토급 발급시키는 과정에서 DB 이용 = @Transactional선언
* 테스트에서는 자동 롤백 수행
* */

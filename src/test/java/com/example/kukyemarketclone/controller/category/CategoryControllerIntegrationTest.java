package com.example.kukyemarketclone.controller.category;

import com.example.kukyemarketclone.dto.category.CategoryCreateRequest;
import com.example.kukyemarketclone.dto.sign.SignInResponse;
import com.example.kukyemarketclone.entity.category.Category;
import com.example.kukyemarketclone.init.TestInitDB;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.service.sign.SignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.example.kukyemarketclone.factory.dto.CategoryCreateRequestFactory.createCategoryCreateRequest;
import static com.example.kukyemarketclone.factory.dto.SignInRequestFactory.createSignInRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class CategoryControllerIntegrationTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TestInitDB initDB;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    SignService signService;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        initDB.InitDB();
    }

    @Test
    void readAllTest() throws Exception{
        //given, when, then
        mockMvc.perform(
                get("/api/categories"))
                .andExpect(status().isOk());
    }

    @Test
    void createTest() throws Exception{
        //given
        CategoryCreateRequest req = createCategoryCreateRequest();
        SignInResponse adminSignInRes = signService.signIn(createSignInRequest(initDB.getAdminEmail(),initDB.getPassword()));
        int beforeSize = categoryRepository.findAll().size();

        //when, then
        mockMvc.perform(
                post("/api/categories")
                        .header("Authorization",adminSignInRes.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        List<Category> result = categoryRepository.findAll();
        assertThat(result.size()).isEqualTo(beforeSize +1);

    }

    @Test
    void createUnauthorizedByNoneTokenTest() throws Exception{
        //given
        CategoryCreateRequest req = createCategoryCreateRequest();

        //when, then
        mockMvc.perform(
                post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void createAccessDeniedByNormalMemberTest() throws Exception{
        //given
        CategoryCreateRequest req = createCategoryCreateRequest();
        SignInResponse normalMemberSignInRes = signService.signIn(createSignInRequest(initDB.getMember1Email(),initDB.getPassword()));

        //when, then
        mockMvc.perform(
                post("/api/categories")
                        .header("Authorization",normalMemberSignInRes.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }

    @Test
    void deleteTest() throws Exception{
        //given
        Long id = categoryRepository.findAll().get(0).getId();
        SignInResponse adminSignInResponse = signService.signIn(createSignInRequest(initDB.getAdminEmail(),initDB.getPassword()));

        //when, then
        mockMvc.perform(
                delete("/api/categories/{id}",id)
                        .header("Authorization",adminSignInResponse.getAccessToken()))
                .andExpect(status().isOk());

        List<Category> result = categoryRepository.findAll();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void deleteAccessDeniedByNormalMemberTest() throws Exception{
        //given
        Long id = categoryRepository.findAll().get(0).getId();
        SignInResponse normalMemberSignInResponse = signService.signIn(createSignInRequest(initDB.getMember1Email(),initDB.getPassword()));

        //when,then
        mockMvc.perform(
                delete("/api/categories/{id}",id)
                        .header("Authorization",normalMemberSignInResponse.getAccessToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }


}

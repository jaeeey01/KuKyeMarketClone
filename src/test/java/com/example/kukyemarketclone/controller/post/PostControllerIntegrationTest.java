package com.example.kukyemarketclone.controller.post;

import com.example.kukyemarketclone.dto.post.PostCreateRequest;
import com.example.kukyemarketclone.dto.post.PostReadCondition;
import com.example.kukyemarketclone.dto.sign.SignInResponse;
import com.example.kukyemarketclone.entity.category.Category;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.post.Post;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.exception.PostNotFoundException;
import com.example.kukyemarketclone.init.TestInitDB;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.post.PostRepository;
import com.example.kukyemarketclone.service.post.PostService;
import com.example.kukyemarketclone.service.sign.SignService;
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

import static com.example.kukyemarketclone.factory.dto.PostCreateRequestFactory.createPostCreateRequest;
import static com.example.kukyemarketclone.factory.dto.PostReadConditionFactory.createPostReadCondition;
import static com.example.kukyemarketclone.factory.dto.SignInRequestFactory.createSignInRequest;
import static com.example.kukyemarketclone.factory.entity.PostFactory.createPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class PostControllerIntegrationTest {// aop를 통한 게시글 작성자 주입 검증 포함

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
    PostRepository postRepository;
    @Autowired
    SignService signService;
    @Autowired
    PostService postService;

    Member member1, member2, admin;
    Category category;

    @BeforeEach
    void beforeEach(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        initDB.InitDB();
        member1 = memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);
        member2 = memberRepository.findByEmail(initDB.getMember2Email()).orElseThrow(MemberNotFoundException::new);
        admin = memberRepository.findByEmail(initDB.getAdminEmail()).orElseThrow(MemberNotFoundException::new);
        category = categoryRepository.findAll().get(0);
    }

    @Test
    void createTest() throws Exception{
        //given
        SignInResponse signInRes = signService.signIn(createSignInRequest(member1.getEmail(),initDB.getPassword()));
        PostCreateRequest req = createPostCreateRequest("title","content",1000L, member1.getId(), category.getId(), List.of());

        //when, then
        mockMvc.perform(
                multipart("/api/posts")
                        .param("title",req.getTitle())
                        .param("content",req.getContent())
                        .param("price",String.valueOf(req.getPrice()))
                        .param("categoryId",String.valueOf(req.getCategoryId()))
                        .with(RequestPostProcessor ->{
                            RequestPostProcessor.setMethod("POST");
                            return RequestPostProcessor;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization",signInRes.getAccessToken()))
                .andExpect(status().isCreated());

        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("title");
        assertThat(post.getContent()).isEqualTo("content");
        assertThat(post.getMember().getId()).isEqualTo(member1.getId());
    }

    @Test
    void createUnauthorizedByNoneTokenTest() throws Exception{
        //given
        PostCreateRequest req = createPostCreateRequest("title","content",1000L,member1.getId(),category.getId(),List.of());

        //when, then
        mockMvc.perform(
                multipart("/api/posts")
                        .param("title",req.getTitle())
                        .param("content",req.getContent())
                        .param("price",String.valueOf(req.getPrice()))
                        .param("categoryId",String.valueOf(req.getCategoryId()))
                        .with(RequestPostProcessor -> {
                            RequestPostProcessor.setMethod("POST");
                            return RequestPostProcessor;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readTest() throws Exception{
        //given
        Post post = postRepository.save(createPost(member1,category));

        //when, then
        mockMvc.perform(
                get("/api/posts/{id}",post.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteByResourceOwnerTest() throws Exception{
        //given
        Post post = postRepository.save(createPost(member1,category));
        SignInResponse signInRes = signService.signIn(createSignInRequest(member1.getEmail(),initDB.getPassword()));

        //when, then
        mockMvc.perform(
                delete("/api/posts/{id}",post.getId())
                        .header("Authorization",signInRes.getAccessToken()))
                .andExpect(status().isOk());

        assertThatThrownBy( () -> postService.read(post.getId())).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void deleteByAdminTest() throws Exception{
        //given
        Post post = postRepository.save(createPost(member1, category));
        SignInResponse AdminSignInRes = signService.signIn(createSignInRequest(admin.getEmail(),initDB.getPassword()));

        //when, then
        mockMvc.perform(
                delete("/api/posts/{id}",post.getId())
                        .header("Authorization",AdminSignInRes.getAccessToken()))
                .andExpect(status().isOk());

        assertThatThrownBy(() -> postService.read(post.getId())).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void deleteAccessDeniedByNotResourceOwnerTest() throws Exception {
        //given
        Post post = postRepository.save(createPost(member1,category));
        SignInResponse notOwnerSignInRes = signService.signIn(createSignInRequest(member2.getEmail(),initDB.getPassword()));

        //when, then
        mockMvc.perform(
                delete("/api/posts/{id}",post.getId())
                        .header("Authorization",notOwnerSignInRes.getAccessToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUnauthorizedByNoneTokenTest() throws Exception{
        //given
        Post post = postRepository.save(createPost(member1, category));

        //when, then
        mockMvc.perform(
                delete("/api/posts/{id}",post.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateByResourceOwnerTest() throws Exception{
        // given
        SignInResponse signInRes = signService.signIn(createSignInRequest(member1.getEmail(), initDB.getPassword()));
        Post post = postRepository.save(createPost(member1, category));
        String updatedTitle = "updatedTitle";
        String updatedContent = "updatedContent";
        Long updatedPrice = 1234L;

        // when, then
        mockMvc.perform(
                multipart("/api/posts/{id}", post.getId())
                        .param("title", updatedTitle)
                        .param("content", updatedContent)
                        .param("price", String.valueOf(updatedPrice))
                        .with(requestPostProcessor -> {
                            requestPostProcessor.setMethod("PUT");
                            return requestPostProcessor;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isOk());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);
        assertThat(updatedPost.getTitle()).isEqualTo(updatedTitle);
        assertThat(updatedPost.getContent()).isEqualTo(updatedContent);
        assertThat(updatedPost.getPrice()).isEqualTo(updatedPrice);

    }

    @Test
    void updateByAdminTest() throws Exception{
        //given
        SignInResponse adminSignInRes = signService.signIn(createSignInRequest(admin.getEmail(), initDB.getPassword()));
        Post post = postRepository.save(createPost(member1, category));
        String updatedTitle = "updatedTitle";
        String updateContent ="updateContent";
        Long updatedPrice = 1234L;

        //when, then
        mockMvc.perform(
                multipart("/api/posts/{id}",post.getId())
                .param("title",updatedTitle)
                .param("content",updateContent)
                .param("price",String.valueOf(updatedPrice))
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PUT");
                    return requestPostProcessor;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization",adminSignInRes.getAccessToken()))
                .andExpect(status().isOk());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);
        assertThat(updatedPost.getTitle()).isEqualTo(updatedTitle);
        assertThat(updatedPost.getContent()).isEqualTo(updateContent);
        assertThat(updatedPost.getPrice()).isEqualTo(updatedPrice);
    }

    @Test
    void updateUnauthorizedByNoneTokenTest() throws Exception{
        //given
        Post post = postRepository.save(createPost(member1, category));
        String updatedTitle = "updatedTitle";
        String updateContent ="updateContent";
        Long updatedPrice = 1234L;

        //when, then
        mockMvc.perform(
                multipart("/api/posts/{id}",post.getId())
                .param("title",updatedTitle)
                .param("content",updateContent)
                .param("price",String.valueOf(updatedPrice))
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PUT");
                    return requestPostProcessor;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateAccessDeniedByNotResourceOwnerTest() throws Exception {
        //given
        SignInResponse notOwnerSignInRes = signService.signIn(createSignInRequest(member2.getEmail(),initDB.getPassword()));
        Post post = postRepository.save(createPost(member1, category));
        String updatedTitle = "updatedTitle";
        String updateContent ="updateContent";
        Long updatedPrice = 1234L;

        //when, then
        mockMvc.perform(
                multipart("/api/posts/{id}",post.getId())
                .param("title",updatedTitle)
                .param("content",updateContent)
                .param("price",String.valueOf(updatedPrice))
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PUT");
                    return requestPostProcessor;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization",notOwnerSignInRes.getAccessToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void readAllTest() throws Exception{
        //given
        PostReadCondition cond = createPostReadCondition(0,1);

        //when, then
        mockMvc.perform(
                get("/api/posts")
                        .param("page",String.valueOf(cond.getPage())).param("size",String.valueOf(cond.getSize()))
                        .param("categoryId",String.valueOf(1),String.valueOf(2))
                        .param("memberId",String.valueOf(1),String.valueOf(2))
        )
                .andExpect(status().isOk());
    }
}

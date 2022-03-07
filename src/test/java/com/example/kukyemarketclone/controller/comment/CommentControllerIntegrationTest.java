package com.example.kukyemarketclone.controller.comment;

import com.example.kukyemarketclone.dto.comment.CommentCreateRequest;
import com.example.kukyemarketclone.dto.comment.CommentDto;
import com.example.kukyemarketclone.dto.sign.SignInResponse;
import com.example.kukyemarketclone.entity.category.Category;
import com.example.kukyemarketclone.entity.comment.Comment;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.post.Post;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.init.TestInitDB;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import com.example.kukyemarketclone.repository.comment.CommentRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.post.PostRepository;
import com.example.kukyemarketclone.service.comment.CommentService;
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
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.kukyemarketclone.factory.dto.CommentCreateRequestFactory.createCommentCreateRequest;
import static com.example.kukyemarketclone.factory.dto.CommentReadConditionFactory.createCommentReadCondition;
import static com.example.kukyemarketclone.factory.dto.SignInRequestFactory.createSignInRequest;
import static com.example.kukyemarketclone.factory.entity.CommentFactory.createComment;
import static com.example.kukyemarketclone.factory.entity.PostFactory.createPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
class CommentControllerIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired MockMvc mockMvc;

    @Autowired TestInitDB initDB;
    @Autowired CategoryRepository categoryRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired CommentService commentService;
    @Autowired PostRepository postRepository;
    @Autowired SignService signService;
    ObjectMapper objectMapper = new ObjectMapper();

    Member member1, member2, admin;
    Category category;
    Post post;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        initDB.InitDB();

        member1 = memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);
        member2 = memberRepository.findByEmail(initDB.getMember2Email()).orElseThrow(MemberNotFoundException::new);
        admin = memberRepository.findByEmail(initDB.getAdminEmail()).orElseThrow(MemberNotFoundException::new);
        category = categoryRepository.findAll().get(0);
        post = postRepository.save(createPost(member1, category));
    }

    @Test
    void readAllTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                get("/api/comments").param("postId", String.valueOf(1)))
                .andExpect(status().isOk());
    }

    @Test
    void createTest() throws Exception {
        // given
        CommentCreateRequest req = createCommentCreateRequest("content", post.getId(), null, null);
        SignInResponse signInRes = signService.signIn(createSignInRequest(initDB.getMember1Email(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                post("/api/comments")
                        .header("Authorization", signInRes.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        List<CommentDto> result = commentService.readAll(createCommentReadCondition(post.getId()));
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void createUnauthorizedByNoneTokenTest() throws Exception {
        // given
        CommentCreateRequest req = createCommentCreateRequest("content", post.getId(), member1.getId(), null);

        // when, then
        mockMvc.perform(
                post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void deleteByResourceOwnerTest() throws Exception {
        // given
        Comment comment = commentRepository.save(createComment(member1, post, null));
        SignInResponse signInRes = signService.signIn(createSignInRequest(member1.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                delete("/api/comments/{id}", comment.getId())
                        .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isOk());

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    void deleteByAdminTest() throws Exception {
        // given
        Comment comment = commentRepository.save(createComment(member1, post, null));
        SignInResponse adminSignInRes = signService.signIn(createSignInRequest(admin.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                delete("/api/comments/{id}", comment.getId())
                        .header("Authorization", adminSignInRes.getAccessToken()))
                .andExpect(status().isOk());

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    void deleteUnauthorizedByNoneTokenTest() throws Exception {
        // given
        Comment comment = commentRepository.save(createComment(member1, post, null));

        // when, then
        mockMvc.perform(delete("/api/comments/{id}", comment.getId()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void deleteAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given
        Comment comment = commentRepository.save(createComment(member1, post, null));
        SignInResponse notOwnerSignInRes = signService.signIn(createSignInRequest(member2.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                delete("/api/comments/{id}", comment.getId())
                        .header("Authorization", notOwnerSignInRes.getAccessToken()))
                .andExpect(status().is3xxRedirection());
    }
}
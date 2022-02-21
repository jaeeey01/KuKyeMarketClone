package com.example.kukyemarketclone.controller.post;

import com.example.kukyemarketclone.advice.ExceptionAdvice;
import com.example.kukyemarketclone.dto.post.PostCreateRequest;
import com.example.kukyemarketclone.exception.CategoryNotFoundException;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.exception.UnsupportedImageFormatException;
import com.example.kukyemarketclone.service.post.PostService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.example.kukyemarketclone.factory.dto.PostCreateRequestFactory.createPostCreateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerAdviceTest {

    @InjectMocks PostController postController;
    @Mock
    PostService postService;
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach(){
        mockMvc = MockMvcBuilders.standaloneSetup(postController).setControllerAdvice(new ExceptionAdvice()).build();
    }

    @Test
    void createExceptionByMemberNotFoundException() throws Exception{
        //given
        given(postService.create(any())).willThrow(MemberNotFoundException.class);

        //when, then
        performCreate()
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(-1007));
    }

    @Test
    void createExceptionByCategoryNotFoundException() throws Exception{
        //given
        given(postService.create(any())).willThrow(CategoryNotFoundException.class);

        //when, then
        performCreate()
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(-1010));
    }

    @Test
    void createExceptionByUnsupportedImageFormatException() throws Exception{
        //given
        given(postService.create(any())).willThrow(UnsupportedImageFormatException.class);

        //when, then
        performCreate()
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(-1013));
    }


    private ResultActions performCreate() throws Exception{
        PostCreateRequest req = createPostCreateRequest();
        return mockMvc.perform(
                multipart("/api/posts")
                        .param("title",req.getTitle())
                        .param("content",req.getContent())
                        .param("price",String.valueOf(req.getPrice()))
                        .param("categoryId",String.valueOf(req.getCategoryId()))
                        .with(requestPostProcessor ->{
                            requestPostProcessor.setMethod("POST");
                            return requestPostProcessor;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        );
    }
}

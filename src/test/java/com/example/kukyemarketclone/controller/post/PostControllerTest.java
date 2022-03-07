package com.example.kukyemarketclone.controller.post;

import com.example.kukyemarketclone.dto.post.PostCreateRequest;
import com.example.kukyemarketclone.dto.post.PostReadCondition;
import com.example.kukyemarketclone.dto.post.PostUpdateRequest;
import com.example.kukyemarketclone.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.kukyemarketclone.factory.dto.PostCreateRequestFactory.createPostCreateRequestWithImages;
import static com.example.kukyemarketclone.factory.dto.PostReadConditionFactory.createPostReadCondition;
import static com.example.kukyemarketclone.factory.dto.PostUpdateRequestFactory.createPostUpdateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @InjectMocks PostController postController;
    @Mock
    PostService postService;
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach(){
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void createTest() throws Exception{
        //given
        //요청을 통해 컨트롤러에서 @ModelAttribute로 전달받을 PostCreateRequest를 캡처할 수 있도록 선언
        ArgumentCaptor<PostCreateRequest> postCreateRequestArgumentCaptor = ArgumentCaptor.forClass(PostCreateRequest.class);

        List<MultipartFile> imageFiles = List.of(
                new MockMultipartFile("test1","test1.PNG", MediaType.IMAGE_PNG_VALUE,"test1".getBytes()),
                new MockMultipartFile("test2","test2.PNG",MediaType.IMAGE_PNG_VALUE,"test2".getBytes())
        );
        PostCreateRequest req = createPostCreateRequestWithImages(imageFiles);

        //when, then
        mockMvc.perform(
                multipart("/api/posts")
                        .file("images",imageFiles.get(0).getBytes())//multipart()를 이용하여 multipart/form-data 요청을 보내기 위한 데이터 지정
                        .file("images",imageFiles.get(1).getBytes())
                        .param("title",req.getTitle())
                        .param("content", req.getContent())
                        .param("price",String.valueOf(req.getPrice()))
                        .param("categoryId",String.valueOf(req.getCategoryId()))
                        .with(requestPostProcessor -> {// 해당 요청은 post 메소드 지정
                            requestPostProcessor.setMethod("POST");
                            return requestPostProcessor;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        //Mock으로 만들어둔 postService.create가 호출되는지 확인, 전달되는 인자 캡처
        verify(postService).create(postCreateRequestArgumentCaptor.capture());

        //캡처 한 값을 꺼내고, 정상적으로 두건의 이미지가 업로드 된 것인지 검증
        PostCreateRequest capturedRequest = postCreateRequestArgumentCaptor.getValue();
        assertThat(capturedRequest.getImages().size()).isEqualTo(2);
    }

    @Test
    void readTest() throws  Exception{
        //given
        Long id = 1L;

        //when, then
        mockMvc.perform(
                get("/api/posts/{id}",id))
                .andExpect(status().isOk());
        verify(postService).read(id);
    }

    @Test
    void deleteTest() throws Exception{
        //given
        Long id = 1L;

        //when, then
        mockMvc.perform(
                delete("/api/posts/{id}",id))
                .andExpect(status().isOk());

        verify(postService).delete(id);
    }

    @Test
    void updateTest() throws Exception{
        //given
        ArgumentCaptor<PostUpdateRequest> postUpdateRequestArgumentCaptor = ArgumentCaptor.forClass(PostUpdateRequest.class);

        List<MultipartFile> addedImages = List.of(
            new MockMultipartFile("test1","test1.PNG",MediaType.IMAGE_PNG_VALUE,"test1".getBytes()),
            new MockMultipartFile("test2","test2.PNG",MediaType.IMAGE_PNG_VALUE,"test2".getBytes())
        );

        List<Long> deletedImages = List.of(1L, 2L);

        PostUpdateRequest req = createPostUpdateRequest("title","content",1234L,addedImages,deletedImages);

        //when, then
        mockMvc.perform(
                multipart("/api/posts/{id}",1L)
                .file("addedImages",addedImages.get(0).getBytes())
                .file("addedImages",addedImages.get(1).getBytes())
                .param("deletedImages",String.valueOf(deletedImages.get(0)),String.valueOf(deletedImages.get(1)))
                .param("title",req.getTitle())
                .param("content",req.getContent())
                .param("price",String.valueOf(req.getPrice()))
                .with(requestPostProcessor ->{
                    requestPostProcessor.setMethod("PUT");
                    return requestPostProcessor;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
                .andExpect(status().isOk());
        verify(postService).update(anyLong(),postUpdateRequestArgumentCaptor.capture());

        PostUpdateRequest capturedRequest = postUpdateRequestArgumentCaptor.getValue();
        List<MultipartFile> capturedAddedImages = capturedRequest.getAddedImages();
        assertThat(capturedAddedImages.size()).isEqualTo(2);

        List<Long> capturedDeletedImages = capturedRequest.getDeletedImages();
        assertThat(capturedDeletedImages.size()).isEqualTo(2);
        assertThat(capturedDeletedImages).contains(deletedImages.get(0),deletedImages.get(1));

    }

    @Test
    void readAllTest() throws Exception{
        //given
        PostReadCondition cond = createPostReadCondition(0,1,List.of(1L,2L),List.of(1L,2L));

        //when, then
        mockMvc.perform(
                get("/api/posts")
                        .param("page",String.valueOf(cond.getPage())).param("size",String.valueOf(cond.getSize()))
                        .param("categoryId",String.valueOf(cond.getCategoryId().get(0)),String.valueOf(cond.getCategoryId().get(1)))
                        .param("memberId",String.valueOf(cond.getMemberId().get(0)),String.valueOf(cond.getMemberId().get(1)))
        )
                .andExpect(status().isOk());

        verify(postService).readAll(cond);
    }
}
package com.example.kukyemarketclone.service.post;

import com.example.kukyemarketclone.dto.post.PostCreateRequest;
import com.example.kukyemarketclone.exception.CategoryNotFoundException;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.exception.UnsupportedImageFormatException;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.post.PostRepository;
import com.example.kukyemarketclone.service.file.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.kukyemarketclone.factory.dto.PostCreateRequestFactory.createPostCreateRequest;
import static com.example.kukyemarketclone.factory.dto.PostCreateRequestFactory.createPostCreateRequestWithImages;
import static com.example.kukyemarketclone.factory.entity.CategoryFactory.createCategory;
import static com.example.kukyemarketclone.factory.entity.ImageFactory.createImage;
import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;
import static com.example.kukyemarketclone.factory.entity.PostFactory.createPostWithImages;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks PostService postService;
    @Mock
    PostRepository postRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    FileService fileService;

    @Test
    void createTest(){
        //given
        PostCreateRequest req = createPostCreateRequest();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(createCategory()));
        given(postRepository.save(any())).willReturn(createPostWithImages(
                IntStream.range(0, req.getImages().size()).mapToObj(i -> createImage()).collect(toList()))
        );

        //when
        postService.create(req);

        //then
        verify(postRepository).save(any());
        verify(fileService, times(req.getImages().size())).upload(any(),anyString());
    }

    @Test
    void createExceptionByMemberNotFoundTest(){
        //given
        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        //when, then
        assertThatThrownBy( () -> postService.create(createPostCreateRequest())).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void createExceptionByCategoryNotFoundTest(){
        //given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        //when, then
        assertThatThrownBy( () -> postService.create(createPostCreateRequest())).isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void createExceptionByUnsupportedImageFormatExceptionTest(){
        //given
        PostCreateRequest req = createPostCreateRequestWithImages(
                List.of(new MockMultipartFile("test","test.txt", MediaType.TEXT_PLAIN_VALUE,"test".getBytes()))
        );

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(createCategory()));

        //when, then
        assertThatThrownBy( () -> postService.create(req)).isInstanceOf(UnsupportedImageFormatException.class);
    }

}
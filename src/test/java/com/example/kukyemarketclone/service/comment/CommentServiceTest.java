package com.example.kukyemarketclone.service.comment;

import com.example.kukyemarketclone.dto.comment.CommentDto;
import com.example.kukyemarketclone.exception.CommentNotFoundException;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.exception.PostNotFoundException;
import com.example.kukyemarketclone.repository.comment.CommentRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.example.kukyemarketclone.factory.dto.CommentCreateRequestFactory.createCommentCreateRequest;
import static com.example.kukyemarketclone.factory.dto.CommentCreateRequestFactory.createCommentCreateRequestWithParentId;
import static com.example.kukyemarketclone.factory.dto.CommentReadConditionFactory.createCommentReadCondition;
import static com.example.kukyemarketclone.factory.entity.CommentFactory.createComment;
import static com.example.kukyemarketclone.factory.entity.CommentFactory.createDeletedComment;
import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;
import static com.example.kukyemarketclone.factory.entity.PostFactory.createPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks CommentService commentService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    PostRepository postRepository;

    @Test
    void readAllTest() {
        //given
        given(commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(anyLong()))
                .willReturn(
                        List.of(createComment(null),
                                createComment(null)
                        )
                );

        //when
        List<CommentDto> result = commentService.readAll(createCommentReadCondition());

        //then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void readAllDeletedCommentTest() {
        //given
        given(commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(anyLong()))
                .willReturn(
                        List.of(createDeletedComment(null),
                                createDeletedComment(null)
                        )
                );

        //when
        List<CommentDto> result = commentService.readAll(createCommentReadCondition());

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getContent()).isNull();
        assertThat(result.get(0).getMember()).isNull();
    }

    @Test
    void createTest(){
        //given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(createPost()));

        //when
        commentService.create(createCommentCreateRequest());

        //then
        verify(commentRepository).save(any());
    }


    @Test
    void createExceptionByMemberNotFoundTest() {
        //given
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy( () -> commentService.create(createCommentCreateRequest()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void createExceptionByPostNotFountTest(){
        //given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> commentService.create(createCommentCreateRequest()))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void createExceptionByCommentNotFoundTest(){
        //given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(createPost()));
        given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> commentService.create(createCommentCreateRequestWithParentId(1L)))
                .isInstanceOf(CommentNotFoundException.class);
    }
}
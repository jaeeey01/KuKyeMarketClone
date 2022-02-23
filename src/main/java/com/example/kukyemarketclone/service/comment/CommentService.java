package com.example.kukyemarketclone.service.comment;

import com.example.kukyemarketclone.dto.comment.CommentCreateRequest;
import com.example.kukyemarketclone.dto.comment.CommentDto;
import com.example.kukyemarketclone.dto.comment.CommentReadCondition;
import com.example.kukyemarketclone.entity.comment.Comment;
import com.example.kukyemarketclone.exception.PostNotFoundException;
import com.example.kukyemarketclone.repository.comment.CommentRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public List<CommentDto> readAll(CommentReadCondition cond){
        return CommentDto.toDtoList(
                commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(cond.getPostId())
        );
    }

    @Transactional
    public void create(CommentCreateRequest req){
        commentRepository.save(CommentCreateRequest.toEntity(req, memberRepository, postRepository, commentRepository));

    }

    @Transactional
    public void delete(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(PostNotFoundException::new);
        comment.findDeletableComment().ifPresentOrElse(commentRepository::delete, comment::delete);
    }
}

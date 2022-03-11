package com.example.kukyemarketclone.service.comment;

import com.example.kukyemarketclone.dto.comment.CommentCreateRequest;
import com.example.kukyemarketclone.dto.comment.CommentDto;
import com.example.kukyemarketclone.dto.comment.CommentReadCondition;
import com.example.kukyemarketclone.entity.comment.Comment;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.post.Post;
import com.example.kukyemarketclone.exception.CommentNotFoundException;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.exception.PostNotFoundException;
import com.example.kukyemarketclone.repository.comment.CommentRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    private final ApplicationEventPublisher publisher;

    public List<CommentDto> readAll(CommentReadCondition cond){
        return CommentDto.toDtoList(
                commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(cond.getPostId())
        );
    }

    @Transactional
    public void create(CommentCreateRequest req){
        Member member = memberRepository.findById(req.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Post post = postRepository.findById(req.getPostId()).orElseThrow(PostNotFoundException::new);
        Comment parent = Optional.ofNullable(req.getParentId())
                .map(id -> commentRepository.findById(id).orElseThrow(CommentNotFoundException::new))
                .orElse(null);

        Comment comment = commentRepository.save(new Comment(req.getContent(), member, post, parent));
        comment.publishCreatedEvent(publisher); //댓글 알람 이벤트 발생
        log.info("CommentService.create");

    }

    @Transactional
    @PreAuthorize("@commentGuard.check(#id)")
    public void delete(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(PostNotFoundException::new);
        comment.findDeletableComment().ifPresentOrElse(commentRepository::delete, comment::delete);
    }
}

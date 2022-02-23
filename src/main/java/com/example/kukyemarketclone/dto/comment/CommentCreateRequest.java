package com.example.kukyemarketclone.dto.comment;

import com.example.kukyemarketclone.entity.comment.Comment;
import com.example.kukyemarketclone.exception.CommentNotFoundException;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.exception.PostNotFoundException;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import com.example.kukyemarketclone.repository.comment.CommentRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.post.PostRepository;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.util.Optional;

@ApiModel(value = "댓글 생성 요청")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateRequest {

    @ApiModelProperty(value = "댓글", notes = "댓글을 입력해주세요", required = true, example = "my comment")
    @NotBlank(message = "댓글을 입력해주세요")
    private String content;

    @ApiModelProperty(value = "게시글 아이디", notes = "게시글 아이디를 입력해주세요", example = "7")
    @NotNull(message = "게시글 아이디를 입력해주세요")
    @Positive(message = "올바른 게시글 아이디를 입력해주세요")
    private Long postId;

    @ApiModelProperty(hidden = true)
    @Null// 서버에서 직접 주입 할 것이기에 @null
    private Long memberId;

    @ApiModelProperty(value = "부모 댓글 아이디", notes = "부모 댓글 아이디를 입력해주세요", example = "7")
    private Long parentId;

    public static Comment toEntity(CommentCreateRequest req, MemberRepository memberRepository, PostRepository postRepository, CommentRepository commentRepository){
        return new Comment(
                req.content,
                memberRepository.findById(req.getMemberId()).orElseThrow(MemberNotFoundException::new),
                postRepository.findById(req.postId).orElseThrow(PostNotFoundException::new),
                Optional.ofNullable(req.parentId)
                        .map(id -> commentRepository.findById(id).orElseThrow(CommentNotFoundException::new))
                        .orElse(null)
                //ofNullalbe에 값이
                // null : map 수행 X, orElse 반환
                // 정상 : 해당 Comment 반환되어 부모로 지정
        );
    }

}

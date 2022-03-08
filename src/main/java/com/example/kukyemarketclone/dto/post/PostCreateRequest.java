package com.example.kukyemarketclone.dto.post;

import com.example.kukyemarketclone.entity.post.Image;
import com.example.kukyemarketclone.entity.post.Post;
import com.example.kukyemarketclone.exception.CategoryNotFoundException;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ApiModel(value = "게시글 생성 요청")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    @ApiModelProperty(value = "게시글 제목", notes = "게시글 제목을 입력해주세요", required = true, example = "my title")
    @NotBlank(message = "{postCreateRequest.title.notBlank}")
    private String title;

    @ApiModelProperty(value = "게시글 본문", notes = "게시글 본문을 입력해주세요", required = true, example = "my content")
    @NotBlank(message = "{postCreateRequest.content.notBlank}")
    private String content;

    @ApiModelProperty(value = "가격", notes = "가격을 입력해주세요", required = true, example = "50000")
    @NotNull(message = "{postCreateRequest.price.notNull}")
    @PositiveOrZero(message = "{postCreateRequest.price.positiveOrZero}")
    private Long price;

    @ApiModelProperty(hidden = true)
    @Null
    private Long memberId;//작성자 조작 가능성으로 클라이언트에게 받지않고 AOP를 이용하여 토큰에 저장된 ID를 직접 주입

    @ApiModelProperty(value = "카테고리 아이디", notes = "카테고리 아이디를 입력해주세요", required = true, example = "3")
    @NotNull(message = "{postCreateRequest.categoryId.notNull}")
    @PositiveOrZero(message = "{postCreateRequest.categoryId.positiveOrZero}")
    private Long categoryId;

    @ApiModelProperty(value = "이미지", notes = "이미지를 첨부해주세요")
    // 첨부하기 위한 이미지 목록 - MultipartFile로 가지고 있음
    private List<MultipartFile> images = new ArrayList<>();//이미지가 없을 경우의 NPE 대비하여 비어있는 리스트로 초기화

}

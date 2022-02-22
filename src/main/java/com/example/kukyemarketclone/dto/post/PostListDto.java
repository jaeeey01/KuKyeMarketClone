package com.example.kukyemarketclone.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class PostListDto {
    private Long totalElements;
    private Integer totalPages;
    private boolean hasNext;
    private List<PostSimpleDto> postList;

    public static PostListDto toDto(Page<PostSimpleDto> page){//총 게시글 개수, 총페이지수, 다음 페이지 여부, 실제 페이지 내역
        return new PostListDto(page.getTotalElements(),page.getTotalPages(),page.hasNext(),page.getContent());
    }
}

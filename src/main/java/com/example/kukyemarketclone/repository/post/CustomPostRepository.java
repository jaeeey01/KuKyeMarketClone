package com.example.kukyemarketclone.repository.post;

import com.example.kukyemarketclone.dto.post.PostReadCondition;
import com.example.kukyemarketclone.dto.post.PostSimpleDto;
import org.springframework.data.domain.Page;

public interface CustomPostRepository {
    Page<PostSimpleDto> findAllByCondition(PostReadCondition cond);
}

package com.example.kukyemarketclone.factory.dto;

import com.example.kukyemarketclone.dto.post.PostReadCondition;

import java.util.List;

public class PostReadConditionFactory {
    public static PostReadCondition createPostReadCondition(Integer page, Integer size){
        return new PostReadCondition(page, size, List.of(), List.of());
    }

    public static PostReadCondition createPostReadCondition(Integer page, Integer size, List<Long> categoryIds, List<Long> memberIds){
        return new PostReadCondition(page, size, categoryIds, memberIds);
    }
}

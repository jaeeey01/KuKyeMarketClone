package com.example.kukyemarketclone.factory.entity;

import com.example.kukyemarketclone.entity.category.Category;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.post.Image;
import com.example.kukyemarketclone.entity.post.Post;

import java.util.List;

import static com.example.kukyemarketclone.factory.entity.CategoryFactory.createCategory;
import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;

public class PostFactory {
    public static Post createPost(){
        return createPost(createMember(), createCategory());
    }

    public static Post createPost(Member member, Category category){
       return new Post("title","content",1000L,member,category, List.of());
    }

    public static Post createPostWithImages(Member member, Category category, List<Image> images){
        return new Post("title","content",1000L, member, category, images);
    }

    public static Post createPostWithImages(List<Image> images){
        return new Post("title","content",1000L,createMember(),createCategory(),images);
    }
}

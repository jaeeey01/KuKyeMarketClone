package com.example.kukyemarketclone.factory.entity;

import com.example.kukyemarketclone.entity.comment.Comment;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.post.Post;

import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;
import static com.example.kukyemarketclone.factory.entity.PostFactory.createPost;

public class CommentFactory {
    public static Comment createComment(Comment parent){
        return new Comment("'content",createMember(),createPost(),parent);
    }

    public static Comment createComment(Member member, Post post, Comment parent){
        return new Comment("content",member, post, parent );
    }

    public static Comment createDeletedComment(Comment parent){
        Comment comment = new Comment("content", createMember(), createPost(),parent);
        comment.delete();
        return comment;
    }
}

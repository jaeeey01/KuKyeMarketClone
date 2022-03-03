package com.example.kukyemarketclone.event.comment;

import com.example.kukyemarketclone.dto.member.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentCreatedEvent {
    private MemberDto publisher;    //댓글 작성자 (이벤트 발생자)
    private MemberDto postWriter;   //게시글 작성자
    private MemberDto parentWriter; //상위댓글 작성자
    private String content;         //댓글 내용

}

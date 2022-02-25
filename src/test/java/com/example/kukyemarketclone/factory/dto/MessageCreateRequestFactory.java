package com.example.kukyemarketclone.factory.dto;

import com.example.kukyemarketclone.dto.message.MessageCreateRequest;

public class MessageCreateRequestFactory {
    public static MessageCreateRequest createMessageCreateRequest(){
        return new MessageCreateRequest("content", 1L, 2L);
    }
    public static MessageCreateRequest createMessageCreateRequest(String content, Long memberId, Long receiverId){
        return new MessageCreateRequest(content, memberId, receiverId);
    }
}

package com.example.kukyemarketclone.factory.dto;

import com.example.kukyemarketclone.dto.message.MessageReadCondition;

public class MessageReadConditionFactory {
    public static MessageReadCondition createMessageReadCondition(){
        return new MessageReadCondition(1L,1L,2);
    }
    public static MessageReadCondition createMessageReadCondition(Long memberId, Long lastMessageId, Integer size){
        return new MessageReadCondition(memberId, lastMessageId, size);
    }

}

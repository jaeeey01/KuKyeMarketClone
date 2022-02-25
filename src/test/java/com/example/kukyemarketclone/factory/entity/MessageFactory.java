package com.example.kukyemarketclone.factory.entity;

import com.example.kukyemarketclone.entity.Message.Message;
import com.example.kukyemarketclone.entity.member.Member;

import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;

public class MessageFactory {
    public static Message createMessage(){
        return new Message("content", createMember(), createMember());
    }

    public static Message createMessage(Member sender, Member receiver){
        return new Message("content",sender,receiver);
    }
}

package com.example.kukyemarketclone.factory.entity;

import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.member.Role;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.Collections.emptyList;

public class MemberFactory {

    public static Member createMember(){
        return new Member("email@email.com","123456a!","username","nickname",emptyList());
    }

    public static Member createMember(String email, String password, String username, String nickname){
        return new Member(email, password, username, nickname, emptyList());
    }

    public static Member createMemberWithRoles(List<Role> roles){
        return new Member("email","password","username","nickname",roles);
    }

    public static Member createMemberWithId(Long id){
        Member member = new Member("email@email.com","123456a!","username", "nickname",emptyList());
        ReflectionTestUtils.setField(member,"id",id);
        return member;
    }
}

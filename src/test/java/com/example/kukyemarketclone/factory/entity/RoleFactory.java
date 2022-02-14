package com.example.kukyemarketclone.factory.entity;

import com.example.kukyemarketclone.entity.member.Role;
import com.example.kukyemarketclone.entity.member.RoleType;

public class RoleFactory {

    public static Role createRole(){
        return new Role(RoleType.ROLE_NORMAL);
    }


}

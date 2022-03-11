package com.example.kukyemarketclone.config.security.guard;

import com.example.kukyemarketclone.entity.member.RoleType;

import java.util.List;

public abstract class Guard {
    //동일한 알고리즘은 템플릿 메소드에 정의
    public final boolean check(Long id){
        return hasRole(getRoleType()) || isResourceOwner(id);
    }

    //변화가 필요한 작업은 추상 메소드 호출
    abstract protected List<RoleType> getRoleType();
    abstract protected boolean isResourceOwner(Long id);

    private boolean hasRole(List<RoleType> roleTypes){
        return roleTypes.stream().allMatch(roleType -> AuthHelper.extractMemberRoles().contains(roleType));
    }

}

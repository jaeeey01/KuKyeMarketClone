package com.example.kukyemarketclone.config.security.guard;

import com.example.kukyemarketclone.entity.member.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberGuard extends Guard{//지금 요청한 사용자가 인증 되었는지, 엑세스를 통한 요청인지, 자원 접근 권한을 가지고 있는지 검사

    private List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleType() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        return id.equals(AuthHelper.extractMemberId());
    }

}

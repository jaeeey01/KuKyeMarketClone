package com.example.kukyemarketclone.config.security.guard;

import com.example.kukyemarketclone.entity.member.RoleType;
import com.example.kukyemarketclone.entity.post.Post;
import com.example.kukyemarketclone.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostGuard {//요청자가 관리자 이거나 게시글의 작성자라면 요청을 수행 가능
    //주의 : 반드시 관리자권한 검사가 자원의 소유자 검사보다 먼저 이루어져야함 = DB 접근 비용 최소화

    private final AuthHelper authHelper;
    private final PostRepository postRepository;

    public boolean check(Long id){
        return authHelper.isAuthenticated() && hasAuthority(id);
    }

    private boolean hasAuthority(Long id){
        return hasAdminRole() || isResourceOwner(id);
    }

    private boolean isResourceOwner(Long id){
        Post post = postRepository.findById(id).orElseThrow(() -> {throw new AccessDeniedException("");});
        Long memberId = authHelper.extactMemberId();
        return post.getMember().getId().equals(memberId);
    }

    private boolean hasAdminRole(){
        return authHelper.extractMemberRoles().contains(RoleType.ROLE_ADMIN);
    }
}

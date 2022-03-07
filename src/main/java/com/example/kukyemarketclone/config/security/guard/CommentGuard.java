package com.example.kukyemarketclone.config.security.guard;

import com.example.kukyemarketclone.entity.comment.Comment;
import com.example.kukyemarketclone.entity.member.RoleType;
import com.example.kukyemarketclone.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentGuard extends Guard {
    private final CommentRepository commentRepository;
    private List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleType() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> {throw new AccessDeniedException(""); });
        Long memberId = AuthHelper.extractMemberId();
        return comment.getMember().getId().equals(memberId);
    }
}

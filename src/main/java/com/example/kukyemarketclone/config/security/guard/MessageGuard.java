package com.example.kukyemarketclone.config.security.guard;

import com.example.kukyemarketclone.entity.Message.Message;
import com.example.kukyemarketclone.entity.member.RoleType;
import com.example.kukyemarketclone.repository.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageGuard extends Guard{
    private final MessageRepository messageRepository;
    private List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);


    @Override
    protected List<RoleType> getRoleType() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        Message message = messageRepository.findById(id).orElseThrow(() -> {throw new AccessDeniedException("");
        });
        return message.getSender().getId().equals(AuthHelper.extractMemberId());
    }
}

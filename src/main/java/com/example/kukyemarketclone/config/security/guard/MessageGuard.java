package com.example.kukyemarketclone.config.security.guard;

import com.example.kukyemarketclone.entity.member.RoleType;
import com.example.kukyemarketclone.repository.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageGuard extends Guard{
    private final MessageRepository messageRepository;
    private List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);


    @Override
    protected List<RoleType> getRoleType() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
      return messageRepository.findById(id)
              .map(message -> message.getSender())
              .map(sender -> sender.getId())
              .filter(senderId -> senderId.equals(AuthHelper.extractMemberId()))
              .isPresent();
    }
}

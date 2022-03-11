package com.example.kukyemarketclone.service.message;

import com.example.kukyemarketclone.dto.message.MessageCreateRequest;
import com.example.kukyemarketclone.dto.message.MessageDto;
import com.example.kukyemarketclone.dto.message.MessageListDto;
import com.example.kukyemarketclone.dto.message.MessageReadCondition;
import com.example.kukyemarketclone.entity.Message.Message;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.exception.MessageNotFoundException;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MessageService {
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;


    /*readAllBy OOO
    * 송신자와 수신자는 각기 다른방식으로 목록 조회와 삭제 수행
    * 조회 결과로 받은 slice를 MessageListDto로 변환하여 반환
    * */
    public MessageListDto readAllBySender(MessageReadCondition cond){
        return MessageListDto.toDto(
                messageRepository.findAllBySenderIdOrderByMessageIdDesc(cond.getMemberId(), cond.getLastMessageId(), Pageable.ofSize(cond.getSize()))
        );
    }

    public MessageListDto readAllByReceiver(MessageReadCondition cond){
        return MessageListDto.toDto(
                messageRepository.findAllByReceiverIdOrderByMessageIdDesc(cond.getMemberId(), cond.getLastMessageId(), Pageable.ofSize(cond.getSize()))
        );
    }
    //조회 결과로 받은 Message를 MessageDto로 변환하여 반환
    @PreAuthorize("@messageGuard.check(#id)")
    public MessageDto read(Long id){
        return MessageDto.toDto(
                messageRepository.findWithSenderAndReceiverById(id).orElseThrow(MessageNotFoundException::new)
        );
    }

    //전달받은 MessageCreateRequest를 Message 엔티티로 변환하여 저장
    @Transactional
    public void create(MessageCreateRequest req){
        Member sender = memberRepository.findById(req.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Member receiver = memberRepository.findById(req.getReceiverId()).orElseThrow(MemberNotFoundException::new);
        Message message = new Message(req.getContent(),sender,receiver);
        messageRepository.save(message);
    }

    /*deleteBy 000
    * 전달받은 id의 쪽지를 삭제
    * */
    @Transactional
    @PreAuthorize("@messageSenderGuard.check(#id)")
    public void deleteBySender(Long id){
        delete(id, Message::deleteBySender);
    }

    @Transactional
    @PreAuthorize("@messageReceiverGuard.check(#id)")
    public void deleteByReceiver(Long id){
        delete(id, Message::deleteByReceiver);
    }

    // 송신자 또는 수신자에 따라 삭제표시를 해두고, 송/수신자 모두 삭제 요청한 쪽지면 DB에서 제거
    private void delete(Long id, Consumer<Message> delete){
        Message message = messageRepository.findById(id).orElseThrow(MessageNotFoundException::new);
        delete.accept(message);
        if(message.isDeletable()){
            messageRepository.delete(message);
        }
    }

}

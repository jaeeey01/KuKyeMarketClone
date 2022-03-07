package com.example.kukyemarketclone.repository.message;

import com.example.kukyemarketclone.config.QuerydslConfig;
import com.example.kukyemarketclone.dto.message.MessageSimpleDto;
import com.example.kukyemarketclone.entity.Message.Message;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.exception.MessageNotFoundException;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;
import static com.example.kukyemarketclone.factory.entity.MessageFactory.createMessage;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
class MessageRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired MessageRepository messageRepository;
    @PersistenceContext
    EntityManager em;

    Member sender, receiver;

    @BeforeEach
    void beforeEach(){
        sender = memberRepository.save(createMember("sender@sender.com","sender","sender","sender"));
        receiver = memberRepository.save(createMember("receiver@receiver.com","receiver","receiver","receiver"));
    }

    @Test
    void createAndReadTest(){
        //given
        Message message = messageRepository.save(createMessage(sender,receiver));
        clear();

        //when
        Message foundMsg = messageRepository.findById(message.getId()).orElseThrow(MessageNotFoundException::new);

        //then
        assertThat(foundMsg.getId()).isEqualTo(message.getId());

    }

    @Test
    void deleteTest(){
        //given
        Message message = messageRepository.save(createMessage(sender,receiver));

        //when
        messageRepository.delete(message);

        //then
        assertThat(messageRepository.findById(message.getId())).isEmpty();
    }

    @Test
    void deleteCascadeBySenderTest(){
        //given
        Message message = messageRepository.save(createMessage(sender,receiver));
        clear();

        //when
        memberRepository.deleteById(sender.getId());
        clear();

        //then
        assertThat(messageRepository.findById(message.getId())).isEmpty();


    }

    @Test
    void deleteCascadeByReceiverTest(){
        //given
        Message message = messageRepository.save(createMessage(sender,receiver));
        clear();

        //when
        memberRepository.deleteById(receiver.getId());
        clear();

        //then
        assertThat(messageRepository.findById(message.getId())).isEmpty();
    }

    @Test
    void findWithSenderAndReceiverByIdTest() {
        //given
        Message message = messageRepository.save(createMessage(sender,receiver));
        clear();

        //when
        Message foundMessage = messageRepository.findWithSenderAndReceiverById(message.getId()).orElseThrow(MessageNotFoundException::new);

        //then
        assertThat(foundMessage.getId()).isEqualTo(message.getId());
        assertThat(foundMessage.getSender().getEmail()).isEqualTo(sender.getEmail());
        assertThat(foundMessage.getReceiver().getEmail()).isEqualTo(receiver.getEmail());

    }

    @Test
    void findAllBySenderIdOrderByMessageIdDescTest() {
        //given
        //4건의 쪽지를 생성하고 1건의 쪽지는 삭제 요청 각페이지는 2건씩 조회 = 2페이지 3쪽지 조회 되어야함
        List<Message> messages = IntStream.range(0,4)
                .mapToObj(i -> messageRepository.save(createMessage(sender,receiver))).collect(toList());
        messages.get(2).deleteBySender();
        final int size = 2;
        clear();

        //when
        //첫 페이지를 조회하고, 마지막 쪽지 id를 이용하여 두번째 페이지 조회
        Slice<MessageSimpleDto> result1 = messageRepository.findAllBySenderIdOrderByMessageIdDesc(sender.getId(),Long.MAX_VALUE, Pageable.ofSize(size));
        List<MessageSimpleDto> content1 = result1.getContent();
        Long lastMessageId1 = content1.get(content1.size()-1).getId();

       Slice<MessageSimpleDto> result2 = messageRepository.findAllBySenderIdOrderByMessageIdDesc(sender.getId(),lastMessageId1,Pageable.ofSize(size));
       List<MessageSimpleDto> content2 = result2.getContent();

       //then
        // slice.hasnext를 이용하여 다음페이지 여부 확인,
        // slice.getNumberOfelements 를이용하여 조회된 쪽지의 갯수 확인
        assertThat(result1.hasNext()).isTrue();
        assertThat(result1.getNumberOfElements()).isEqualTo(2);
        assertThat(content1.get(0).getId()).isEqualTo(messages.get(3).getId());
        assertThat(content1.get(1).getId()).isEqualTo(messages.get(1).getId());

        assertThat(result2.hasNext()).isFalse();
        assertThat(result2.getNumberOfElements()).isEqualTo(1);
        assertThat(content2.get(0).getId()).isEqualTo(messages.get(0).getId());
    }

    @Test
    void findAllByReceiverIdOrderByMessageIdDescTest() {
        //given
        List<Message> messages = IntStream.range(0,4)
                .mapToObj(i -> messageRepository.save(createMessage(sender,receiver))).collect(toList());
        messages.get(2).deleteByReceiver();
        final int size = 2;
        clear();

        //when
        Slice<MessageSimpleDto> result1 = messageRepository.findAllByReceiverIdOrderByMessageIdDesc(receiver.getId(),Long.MAX_VALUE,Pageable.ofSize(size));
        List<MessageSimpleDto> content1 = result1.getContent();
        Long lastMessageId1 = content1.get(content1.size()-1).getId();

        Slice<MessageSimpleDto> result2 = messageRepository.findAllByReceiverIdOrderByMessageIdDesc(receiver.getId(),lastMessageId1,Pageable.ofSize(size));
        List<MessageSimpleDto> content2 = result2.getContent();

        //then
        assertThat(result1.hasNext()).isTrue();
        assertThat(result1.getNumberOfElements()).isEqualTo(2);
        assertThat(content1.get(0).getId()).isEqualTo(messages.get(3).getId());
        assertThat(content1.get(1).getId()).isEqualTo(messages.get(1).getId());

        assertThat(result2.hasNext()).isFalse();
        assertThat(result2.getNumberOfElements()).isEqualTo(1);
        assertThat(content2.get(0).getId()).isEqualTo(messages.get(0).getId());
    }

    void clear(){
        em.flush();
        em.clear();
    }
}
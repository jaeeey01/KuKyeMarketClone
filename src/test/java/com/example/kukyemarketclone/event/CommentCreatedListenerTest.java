package com.example.kukyemarketclone.event;

import com.example.kukyemarketclone.dto.alarm.AlarmInfoDto;

import com.example.kukyemarketclone.dto.member.MemberDto;
import com.example.kukyemarketclone.event.comment.CommentCreatedEvent;
import com.example.kukyemarketclone.service.alarm.AlarmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.annotation.Transactional;

import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMemberWithId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest //발행된 이벤트를 리스너가 처리하는 과정을 검증 하기 위해 통합테스트로 진행
@ActiveProfiles(value = "test")
@Transactional// @TransactionEventListener로 이벤트 처리하기에 트랜잭션 흐름에 따라 리스너가 이벤트 처리함
@Commit //테스트에서 @Transactional은 기본적으로 롤백함, 하지만 커밋 하고 난 이후 이벤트리스너 동작 검증을 위해 강제 커밋 지정
public class CommentCreatedListenerTest {
    @Autowired
    ApplicationEventPublisher publisher;

    //필요한 의존성을 MOck로 바꿔줌
    @MockBean(name = "smsAlarmService")
    AlarmService smsAlarmService;
    @MockBean(name = "emailAlarmService")
    AlarmService emailAlarmService;
    @MockBean(name = "lineAlarmService")
    AlarmService lineAlarmService;

    int calledCount;

    @AfterTransaction//트랜잭션이 끝난 후 호출, 각 테스트에서 호출된 횟수를 업데이트 하고 , 실질 검증 하는 곳
    void afterEach(){
        verify(emailAlarmService, times(calledCount)).alarm(any(AlarmInfoDto.class));
        verify(lineAlarmService, times(calledCount)).alarm(any(AlarmInfoDto.class));
        verify(smsAlarmService, times(calledCount)).alarm(any(AlarmInfoDto.class));
    }

    @Test
    void handleCommentCreatedEventTest(){
        //given
        MemberDto publisher = MemberDto.toDto(createMemberWithId(1L));
        MemberDto postWriter = MemberDto.toDto(createMemberWithId(2L));
        MemberDto parentWriter = MemberDto.toDto(createMemberWithId(3L));
        String content = "content";

        //when
        this.publisher.publishEvent(new CommentCreatedEvent(publisher,postWriter,parentWriter,content));

        //then
        calledCount = 2;
    }

    @Test
    void handleCommentCreatedEventWhenPublisherIsPostWriterTest(){
        //given
        MemberDto publisher = MemberDto.toDto(createMemberWithId(1L));
        MemberDto postWriter = MemberDto.toDto(createMemberWithId(1L));
        MemberDto parentWriter = MemberDto.empty();
        String content = "content";

        //when
        this.publisher.publishEvent(new CommentCreatedEvent(publisher,postWriter,parentWriter,content));

        //then
        calledCount = 0;
    }

    @Test
    void handleCommentCreatedEventWhenPublisherIsParentWriterTest(){
        //given
        MemberDto publisher = MemberDto.toDto(createMemberWithId(1L));
        MemberDto postWriter = MemberDto.toDto(createMemberWithId(2L));
        MemberDto parentWriter = MemberDto.toDto(createMemberWithId(1L));
        String content = "content";

        //when
        this.publisher.publishEvent(new CommentCreatedEvent(publisher,postWriter,parentWriter,content));

        //then
        calledCount = 1;
    }

    @Test
    void handleCommentCreatedEventWhenPostWriterIsParentWriterTest(){
        //given
        MemberDto publisher = MemberDto.toDto(createMemberWithId(1L));
        MemberDto postWriter = MemberDto.toDto(createMemberWithId(2L));
        MemberDto parentWriter = MemberDto.toDto(createMemberWithId(2L));
        String content = "content";

        //when
        this.publisher.publishEvent(new CommentCreatedEvent(publisher,postWriter,parentWriter,content));

        //then
        calledCount = 1;
    }

}

package com.example.kukyemarketclone.event.comment;

import com.example.kukyemarketclone.dto.alarm.AlarmInfoDto;
import com.example.kukyemarketclone.dto.member.MemberDto;
import com.example.kukyemarketclone.service.alarm.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentCreatedListener {
    //알람 전송을 위한 의존성 주입받음
    private final AlarmService emailAlarmService;
    private final AlarmService lineAlarmService;
    private final AlarmService smsAlarmService;

    private List<AlarmService> alarmServices = new ArrayList<>();


    @PostConstruct //초기화
    public void postConstruct(){     //주입받은 알람 서비스 리스트에 담아줌
        alarmServices.add(emailAlarmService);
        alarmServices.add(lineAlarmService);
        alarmServices.add(smsAlarmService);
    }

    /* @TransactionalEventListener
     *이벤트를 발생하던 트랜잭션 흐름에 따라 이벤트를 제어 가능
     * 디폴트 설정 사용 : TransactionPhase.AFTER_COMMIT
     * 트린잭션이 커밋된 이후 리스너에서 이벤트 처리
     * */

    /* @Async
     *  커밋 이후 리스너에서 이벤트 처리한다해도 모든 과정은 하나의 스레드에서 수행 됨
     * 이벤트에서 처리하는 과정은 네트워크 요청이나 I/O 작업을 수행하면서 많은비용 생길 수 있음
     *  ex) 본래의 작업이 끝났음에도 불구하고, 응답지연 되는 등 문제 발생 가능
     *  이를 위해 @Async 지정
     *  해당 이벤트를 처리하는 메소드는, 새로운 스레드에서 비동기 처리
     *  = 이벤트를발생했던 메소드는 더이상 블록되지않고, 즉시 응답 가능
     *
     *  **** 이를 활성화 하기 위해서 특별한 설정 필요 -> config.AsyncConfig
     * */

    @TransactionalEventListener
    @Async
    public void handleAlarm(CommentCreatedEvent event){
        //전송할 알람 메시지 생성, 게시글 작성자, 상위 댓글 작성자에게 알람 전송 가능 여부 확인 뒤 알람 전송 수행
        log.info("CommentCreatedListener.handleAlarm");
        String message = generateAlarmMessage(event);
        if(isAbleToSendToPostWriter(event)) alarmTo(event.getPostWriter(), message);
        if(isAbleToSendToParentWriter(event)) alarmTo(event.getParentWriter(), message);
    }

    private void alarmTo(MemberDto memberDto, String message){
        // 알람 서비스를 이용하여 알람 전송 (AlarmInfoDto)
        alarmServices.stream().forEach(alarmService -> alarmService.alarm(new AlarmInfoDto(memberDto, message)));
    }

    private boolean isAbleToSendToPostWriter(CommentCreatedEvent event){
        //댓글의 작성자가 게시글 작성자이거나 게시글의 작성자가 상위 댓글 작성자라면(중복알람방지) 전송 할 필요 없음
        if(!isSameMember(event.getPublisher(), event.getPostWriter())){
            if(hasParent(event)) return !isSameMember(event.getPostWriter(),event.getParentWriter());
            return true;
        }
        return false;
    }

    private boolean isAbleToSendToParentWriter(CommentCreatedEvent event){
        //댓글의 작성자가 상위 댓글의 작성자라면 알람 전송 필요 없음
        return hasParent(event) && !isSameMember(event.getPublisher(),event.getParentWriter());
    }

    private boolean isSameMember(MemberDto a, MemberDto b){
        return Objects.equals(a.getId(),b.getId());
    }

    private boolean hasParent(CommentCreatedEvent event){
        return event.getParentWriter().getId() != null;
    }

    private String generateAlarmMessage(CommentCreatedEvent event){
        //발행자의 닉네임과 댓글 내용으로 알람 메시지 생성
        return event.getPublisher().getNickname() + " : " + event.getContent();
    }


}

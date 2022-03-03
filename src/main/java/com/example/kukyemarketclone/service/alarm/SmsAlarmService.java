package com.example.kukyemarketclone.service.alarm;

import com.example.kukyemarketclone.dto.alarm.AlarmInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsAlarmService implements AlarmService{
    @Override
    public void alarm(AlarmInfoDto infoDto) {
        log.info("{} 에게 문자메시지 전송 ={}",infoDto.getTarget().getUsername(),infoDto.getMessage());//전화번호 정보가 없기에 username 대체
    }
}

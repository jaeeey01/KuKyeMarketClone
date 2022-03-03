package com.example.kukyemarketclone.dto.alarm;

import com.example.kukyemarketclone.dto.member.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlarmInfoDto {
    private MemberDto target;
    private String message;


}

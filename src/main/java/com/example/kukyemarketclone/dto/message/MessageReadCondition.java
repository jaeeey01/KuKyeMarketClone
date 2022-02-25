package com.example.kukyemarketclone.dto.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageReadCondition {
    
    @ApiModelProperty(hidden = true)
    @Null //조회 요청을 보낸 사용자 id는 서버에서 직접 주입
    private Long memberId;
    
    @ApiModelProperty(value = "마지막 쪽지 id", notes = "조회된 쪽지의 마지막 id를 입력해주세요", required = true, example = "7")
    //마지막 쪽지의 id는 첫번째 요청일 경우 누락될 수 있으므로, Long.MAX_VALUE로 초기화
    private Long lastMessageId = Long.MAX_VALUE; 
    
    @ApiModelProperty(value = "페이지 크기",notes = "페이지 크기를 입력해주세요",required = true,example = "10")
    @NotNull(message = "페이지 크기를 입력해주세요")
    @Positive(message = "올바른 페이지 크기를 입력해주세요(1 이상)")
    private Integer size;
}

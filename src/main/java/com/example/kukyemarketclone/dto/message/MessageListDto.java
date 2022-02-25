package com.example.kukyemarketclone.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.List;

@Data
@AllArgsConstructor
public class MessageListDto {   //단순히 조회된 쪽지와 다음페이지가 있는지 가지고 있는 Dto
    private int numberOfElements;
    private boolean hasNext;
    private List<MessageSimpleDto> messageList;

    public static MessageListDto toDto(Slice<MessageSimpleDto> slice){
        return new MessageListDto(slice.getNumberOfElements(), slice.hasNext(), slice.getContent());
    }
}

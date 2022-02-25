package com.example.kukyemarketclone.entity.Message;

import com.example.kukyemarketclone.entity.common.EntityDate;
import com.example.kukyemarketclone.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends EntityDate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @Lob
    private String content;
    
    @Column(nullable = false)
    private boolean deletedBySender;//1
    
    @Column(nullable = false)
    private boolean deletedByReceiver;//1
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member receiver;
    
    public Message(String content, Member sender, Member receiver){
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.deletedBySender = this.deletedByReceiver =false;
    }
    
    //deletedBy OOO  = 송/수신자가 쪽지 삭제 요청시 삭제 표시만 해두기
    public void deleteBySender(){
        this.deletedBySender= true;
    }
    
    public void deleteByReceiver(){
        this.deletedByReceiver = true;
    }
    
    public boolean isDeletable(){ //송/수신자 모두 삭제 요청시 실제 DB에서 제거
        return  isDeletedBySender() && isDeletedByReceiver();
    }
    
    /* 1. deletedByReceiver && deletedBySender
    * 각 사용자는 쪽지의 송수신 내역을 조회 할 수 있음
    *결국 송신자와 수신자는 하나의 쪽지에 대해, 각기 다른 생명주기를 가지고 있음
    * 송신자와 수신자 모두가 쪽지 삭제를 요청했을때만 실제 DB에서 제거
    */
}

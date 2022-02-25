package com.example.kukyemarketclone.repository.message;

import com.example.kukyemarketclone.dto.message.MessageSimpleDto;
import com.example.kukyemarketclone.entity.Message.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message,Long> {
    //단순히 sender 와 receiver 페치 조인 단건 조회
    @Query("select m from Message m left join fetch m.sender left join fetch m.receiver where m.id =:id")
    Optional<Message> findWithSenderAndReceiverById(Long id);

    /* 각사용자의 송신 또는 수신내역 조회 쿼리
    * 마지막 쪽지 id 보다 작은 id 값을 가지는 쪽지만 조회.
    * 송신자, 수신자가 삭제 요청했던 쪽지는 조회되지 않고 최근 순 정렬 위해 쪽지 id로 내림차순 정렬
    * slice = 페이징 처리 결과에 대한 다양한 정보 포함
    * 별도의 카운트 쿼리가 수행되지 않고 pageable의 지정된 크기 + 1로 limit 절을 만들어줌 = 다음페이지가 남아있는지 확인 가능
    *
    * 스크롤을 이용한 페이징처리
    * 전체 페이지 수를 알필요가 없기 때문에 카운트 쿼리 사용 x
    * 지정된 크기만큼 조회 x 1건을 더 조회 = 지정했던 크기와 조회 크기다 다르면 다음페이지 없음음
   *
    *
    * */

    @Query("select new com.example.kukyemarketclone.dto.message.MessageSimpleDto(m.id, m.content, m.receiver.nickname, m.createdAt) " +
            "from Message m left join m.receiver " +
            "where m.sender.id = :senderId and m.id < :lastMessageId and m.deletedBySender = false order by m.id desc")
    Slice<MessageSimpleDto> findAllBySenderIdOrderByMessageIdDesc(Long senderId, Long lastMessageId, Pageable pageable);

    @Query("select new com.example.kukyemarketclone.dto.message.MessageSimpleDto(m.id, m.content, m.sender.nickname, m.createdAt) " +
            "from Message m left join m.sender " +
            "where m.receiver.id = :receiverId and m.id < :lastMessageId and m.deletedByReceiver = false order by m.id desc")
    Slice<MessageSimpleDto> findAllByReceiverIdOrderByMessageIdDesc(Long receiverId, Long lastMessageId, Pageable pageable);

}

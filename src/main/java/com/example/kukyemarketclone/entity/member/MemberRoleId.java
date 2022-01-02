package com.example.kukyemarketclone.entity.member;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberRoleId implements Serializable {

    /* class MemberRoleId
    *  @IdClass로 사용될 클래스는 Serializable을 구현해주고,
    *  엔티티 내에서 composite key로 사용될 필드들을 동일하게 정의
    * */

    /*composite key를 만들때 주의 점
    * composite key를 만들 때는 기본적으로 알파벳순으로 키생성됨
    * key들의 순서가 중요함
    *인덱스 구조가 첫번째 필드로 정렬된 뒤에, 두번째 필드로 정렬되기 때문
    *  + 중복도 높은 필드가 첫번째로 생성되면 필터링 되는 레코드가 적어서 인덱스 효과를 보지 못하게 됨
    *  ex) Role은 몇개 밖에 생성 안되기 때문에 중복도가 높고 Member는 계속 생성되기 때문에 중복도가 낮음
    *   = 알파벳순으로 필드이름 변경
    * */
    private Member member;

    private Role role;
}

package com.example.kukyemarketclone.entity.member;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // MemberRole은 Member에서 set으로 저장되기 때문에 재정의

//여러개의 필드를 Pk로 사용하기위해 선언
// MemberRoleId에 정의된 필드와 동일한 필드를 MemberRole에서 Id로 선언시 composite key(복합키)로 만들 수 있음
@IdClass(MemberRoleId.class)
public class MemberRole {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id")
    private Role role;


}

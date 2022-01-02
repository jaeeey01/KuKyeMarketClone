package com.example.kukyemarketclone.entity.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    /* Class Role
    * MemberRole과 oneTomany관계를 가지지만 Role에서 MemberRole을 조회할 필요는 없기 떄문에
    * 별도로 관계를 명시해 주지않음
    * */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name="role_id")
    private Long id;

    @Enumerated(EnumType.STRING)//DB저장시 String으로 저장
    @Column(nullable = false, unique = true)// unique : 중복되는 RoleType이 생성되는 것을 방지
    private RoleType roleType;

    public Role(RoleType roleType){
        this.roleType = roleType;
    }

}

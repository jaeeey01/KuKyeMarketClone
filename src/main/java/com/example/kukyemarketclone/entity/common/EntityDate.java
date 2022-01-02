package com.example.kukyemarketclone.entity.common;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/* @EntityListeners
* 각각 필드에 @CreatedDate, @LastModifiedDate 지정
* 엔티티 생성, 업뎃시 해당 필드 데이터도 자동업뎃
* 활성화를 위해 KuKyeMarketCloneApplication.class에 @EnableJpaAuditing 추가
* */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass // 엔티티에서 상속받게되면 createAt, ModifiedAt 필드 추가
@Getter
public abstract class EntityDate {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime modifiedAt;

}

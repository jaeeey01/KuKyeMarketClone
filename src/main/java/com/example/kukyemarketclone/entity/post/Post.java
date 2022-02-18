package com.example.kukyemarketclone.entity.post;


import com.example.kukyemarketclone.entity.category.Category;
import com.example.kukyemarketclone.entity.common.EntityDate;
import com.example.kukyemarketclone.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends EntityDate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false)
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable = false)
    @OnDelete( action = OnDeleteAction.CASCADE)
    private Member member;  //member와 category의 생명 주기의 의해 게시글도 제거되도록 변경

   @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Image> images;
    //게시글 저장 시에 이미지도 함께 저장 cascade = persist 설정
    //게시글이 삭제 되면 모든 이미지 삭제 + 게시글 수정시 특정 이미지 제거시 해당 이미지 데이터 제거  = orphanRemoval = true

    public Post(String title, String content, Long price, Member member, Category category, List<Image> images){
        this.title = title;
        this.content = content;
        this.price = price;
        this.member = member;
        this.category = category;
        this.images = new ArrayList<>();
        addImages(images);  //게시글에 포함되어야할 image리스트를 생성자 파라미터로 받으면 addImages라는 메소드를 통해 이미지 저장
    }

    private void addImages(List<Image> added){ // 새로운 이미지 정보 등록
        added.stream().forEach( i -> {images.add(i);//인스턴스 변수 images에 image를 추가
        i.initPost(this);//해당 image에 this(post)등록
        });
    }


}

package com.example.kukyemarketclone.entity.post;

import com.example.kukyemarketclone.exception.UnsupportedImageFormatException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uniqueName;

    @Column(nullable = false)
    private String originName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",nullable = false) //image는 post와 연관 관계가 있을 경우에만 저장 되도록 nullable = false
    @OnDelete(action = OnDeleteAction.CASCADE)//게시글 제거시 이미지 연쇄적 제거
    private Post post;

    private final static String[] supportedExtension = {"jpg","jpeg","gif","bmp","png"};

    public Image(String originName ){
        this.uniqueName = generateUniqueName(extractExtension(originName));//고유 이미지 이름 생성
        this.originName = originName;
    }

    public void initPost(Post post){//post에서 image 추가시 호출 하는 메서드
        //post의 연간 관계에 대한 정보가 없을 경우 등록
        if(this.post == null) {//이미지는 작성 게시글에 소속, this.post가 null 일 때만 초기화
            this.post = post;
        }
    }

    private String generateUniqueName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    private String extractExtension(String originName) {//이미지 이름에서 확장자 추출
        try{
            String ext = originName.substring(originName.lastIndexOf(".")+1);
            if(isSupportedFormat(ext)) return ext;
        }catch (StringIndexOutOfBoundsException e){}
            throw new UnsupportedImageFormatException();
    }

    private boolean isSupportedFormat(String ext) { //지원하는 확장자 인지 확인
        return Arrays.stream(supportedExtension).anyMatch(e -> e.equalsIgnoreCase(ext));
    }


}



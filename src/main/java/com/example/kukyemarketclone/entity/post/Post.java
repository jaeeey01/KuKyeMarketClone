package com.example.kukyemarketclone.entity.post;


import com.example.kukyemarketclone.dto.post.PostUpdateRequest;
import com.example.kukyemarketclone.entity.category.Category;
import com.example.kukyemarketclone.entity.common.EntityDate;
import com.example.kukyemarketclone.entity.member.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

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
    /* update
    * 이미지는 별도의 파일 저장소에 저장되어 있으므로 새롭게 업데이트된 이미지 결과에 대해서 파일저장소에 반영해야함
    * 이를 위해 업데이트의 결과로, 추가된 이미지들에 대한 정보와 삭제된 이미지들에 대한 정보를 반환해줌
    * */
    public ImageUpdatedResult update(PostUpdateRequest req){
        this.title = req.getTitle();
        this.content = req.getContent();
        this.price = req.getPrice();
        ImageUpdatedResult result = findImageUpdatedResult(req.getAddedImages(), req.getDeletedImages());
        addImages(result.getAddedImages());
        deleteImages(result.getDeletedImages());
        return result;
    }

    private void addImages(List<Image> added){ // 새로운 이미지 정보 등록
        added.stream().forEach( i -> {images.add(i);//인스턴스 변수 images에 image를 추가
        i.initPost(this);//해당 image에 this(post)등록
        });
    }

    //this.images에서 삭제될 이미지를 제거
    //파라미터로 전달받은 이미지와 this.images는 영속된 상태일 것이도 orphanRemoval-true에 의해
    //post와 연관관계가 끊어지며 고아 객체가 된 Image는 DB에서도 제거될 것
    private void deleteImages(List<Image> deleted){
        deleted.stream().forEach( di -> this.images.remove(di));
    }

    //업데이트 되어야할 이미지 결과 정보 생성
    private ImageUpdatedResult findImageUpdatedResult(List<MultipartFile> addedImageFiles, List<Long> deletedImageIds){
        List<Image> addedImages = convertImageFilesToImages(addedImageFiles);
        List<Image> deletedImages = convertImageIdsToImages(deletedImageIds);
        return new ImageUpdatedResult(addedImageFiles, addedImages, deletedImages);
    }

    private List<Image> convertImageIdsToImages(List<Long> imageIds){
        return imageIds.stream()
                .map(id -> convertImageIdToImage(id))
                .filter(i -> i.isPresent())
                .map(i -> i.get())
                .collect(toList());
    }

    private Optional<Image> convertImageIdToImage(Long id){
        return this.images.stream().filter(i -> i.getId().equals(id)).findAny();
    }

    private List<Image> convertImageFilesToImages(List<MultipartFile> imageFiles){
        return imageFiles.stream().map(imageFile -> new Image(imageFile.getOriginalFilename())).collect(toList());
    }

    //update를 호출한 클라이언트에게 전달될 이미지 업데이트 결과
    //클라이언트는 이 정보를 가지고, 실제 파일 저장소에서 추가될 이미지는 업로드하고 삭제될 이미지는 삭제함
    @Getter
    @AllArgsConstructor
    public static class ImageUpdatedResult{
        private List<MultipartFile> addedImageFiles;
        private List<Image> addedImages;
        private List<Image> deletedImages;
    }
}

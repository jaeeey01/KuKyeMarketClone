package com.example.kukyemarketclone.service.post;

import com.example.kukyemarketclone.dto.post.*;
import com.example.kukyemarketclone.entity.post.Image;
import com.example.kukyemarketclone.entity.post.Post;
import com.example.kukyemarketclone.exception.PostNotFoundException;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.post.PostRepository;
import com.example.kukyemarketclone.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    @Transactional
    public PostCreateResponse create(PostCreateRequest req){
        Post post = postRepository.save(
            PostCreateRequest.toEntity(
                    req,
                    memberRepository,
                    categoryRepository
            )
        );
        uploadImages(post.getImages(),req.getImages());
        return new PostCreateResponse(post.getId());
    }

    public PostDto read(Long id){
        return PostDto.toDto(postRepository.findById(id).orElseThrow(PostNotFoundException::new));
    }

    /*uploadImages
    * postCreateRequest.toEntity를 이용하여 Post 엔티티를 얻음
    * Post 엔티티는 Image 엔티티를 가지고 있고, Image 엔티티는 각이미지의 고유이름으로 생성
    * 실제 이미지 파일을 가지고 있는 MultipartFile을 Image가 가지고 있는 uniqueName을 파일명으로 하여 파일 저장소에 업로드 해야함
    * Post가 가지고 있는 image 리스트는 MultipartFile 리스트를 이용하여 생성하였기 떄문에 동일한 순서와 길이가 보장
    * 이에 대해 uploadImages로 전달해주고 FileService.upload에 각각의 MultipartFile과 uniquename을 인자로 보내주면서 파일 업로드 수행
    * */
    private void uploadImages(List<Image> images, List<MultipartFile> fileImages){
        IntStream.range(0, images.size()).forEach(i -> fileService.upload(fileImages.get(i), images.get(i).getUniqueName()));
    }

    @Transactional
    public void delete(Long id){
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        deleteImages(post.getImages());
        postRepository.delete(post);
    }

    private void deleteImages(List<Image> images){
        images.stream().forEach(i -> fileService.delete(i.getUniqueName()));
    }

    @Transactional
    public PostUpdateResponse update(Long id, PostUpdateRequest req){
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        Post.ImageUpdatedResult result = post.update(req);
        uploadImages(result.getAddedImages(),result.getAddedImageFiles());
        deleteImages(result.getDeletedImages());
        return new PostUpdateResponse(id);

    }

}

package com.example.kukyemarketclone.repository.post;

import com.example.kukyemarketclone.config.QuerydslConfig;
import com.example.kukyemarketclone.dto.post.PostUpdateRequest;
import com.example.kukyemarketclone.entity.category.Category;

import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.post.Image;
import com.example.kukyemarketclone.entity.post.Post;
import com.example.kukyemarketclone.exception.PostNotFoundException;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.kukyemarketclone.factory.dto.PostUpdateRequestFactory.createPostUpdateRequest;
import static com.example.kukyemarketclone.factory.entity.CategoryFactory.createCategory;
import static com.example.kukyemarketclone.factory.entity.ImageFactory.createImage;
import static com.example.kukyemarketclone.factory.entity.ImageFactory.createImageWithOriginName;
import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;
import static com.example.kukyemarketclone.factory.entity.PostFactory.createPost;
import static com.example.kukyemarketclone.factory.entity.PostFactory.createPostWithImages;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(QuerydslConfig.class)
public class PostRepositoryTest {

    @Autowired PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired ImageRepository imageRepository;
    @PersistenceContext
    EntityManager em;

    Member member;
    Category category;

    @BeforeEach
    void beforeEach(){
        member = memberRepository.save(createMember());
        category = categoryRepository.save(createCategory());
    }

    @Test
    void createAndReadTest(){
        //given
        Post post = postRepository.save(createPost(member, category));
        clear();

        //when
        Post foundPost = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);

        //then
        assertThat(foundPost.getId()).isEqualTo(post.getId());
        assertThat(foundPost.getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    void deleteTest(){
        //given
        Post post = postRepository.save(createPost(member,category));
        clear();

        //when
        postRepository.deleteById(post.getId());
        clear();

        //then
        assertThatThrownBy(() -> postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void createCascadeImageTest(){ //post 생성시 images 연쇄적 생성 여부
        //given
        Post post = postRepository.save(createPostWithImages(member, category, List.of(createImage(),createImage())));
        clear();

        //when
        Post foundPost = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);


        //then
        List<Image> images = foundPost.getImages();
        assertThat(images.size()).isEqualTo(2);
    }

    @Test
    void deleteCascadeImageTest(){//post 삭제시  연쇄적 image 삭제 검증
        //given
        Post post = postRepository.save(createPostWithImages(member, category,List.of(createImage(),createImage())));
        clear();

        //when
        postRepository.deleteById(post.getId());
        clear();

        //then
        List<Image> images = imageRepository.findAll();
        assertThat(images.size()).isZero();
    }

    @Test
    void deleteCascadeByMemberTest(){//member 삭제시 연쇄적 post 삭제 검증
        //given
        postRepository.save(createPostWithImages(member,category,List.of(createImage(),createImage())));
        clear();

        //when
        memberRepository.deleteById(member.getId());
        clear();

        //then
        List<Post> result = postRepository.findAll();
        assertThat(result.size()).isZero();
    }

    @Test
    void deleteCascadeByCategoryTest(){ //category 삭제시 연쇄적 post 삭제 검증
        //given
        postRepository.save(createPostWithImages(member,category,List.of(createImage(),createImage())));
        clear();

        //when
        categoryRepository.deleteById(category.getId());
        clear();

        //then
        List<Post> result = postRepository.findAll();
        assertThat(result.size()).isZero();
    }

    @Test
    void findByIdWithMemberTest(){
        //given
        Post post = postRepository.save(createPost(member, category));

        //when
        Post foundPost = postRepository.findByIdWithMember(post.getId()).orElseThrow(PostNotFoundException::new);

        //then
        Member foundMember = foundPost.getMember();
        assertThat(foundMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    void updateTest(){
        //given
        Image a = createImageWithOriginName("a.jpg");
        Image b = createImageWithOriginName("b.jpg");
        Post post = postRepository.save(createPostWithImages(member,category,List.of(a,b)));
        clear();

        //when
        MockMultipartFile cFile = new MockMultipartFile("c","c.png", MediaType.IMAGE_PNG_VALUE,"cFile".getBytes());
        PostUpdateRequest postUpdateRequest =createPostUpdateRequest("update title","update content",1234L,List.of(cFile),List.of(a.getId()));
        Post foundPost = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);
        foundPost.update(postUpdateRequest);
        clear();

        //then
        Post result = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);
        assertThat(result.getTitle()).isEqualTo(postUpdateRequest.getTitle());
        assertThat(result.getContent()).isEqualTo(postUpdateRequest.getContent());
        assertThat(result.getPrice()).isEqualTo(postUpdateRequest.getPrice());
        List<Image> images = result.getImages();
        List<String> originName = images.stream().map( i -> i.getOriginName()).collect(toList());
        assertThat(images.size()).isEqualTo(2);
        assertThat(originName).contains(b.getOriginName(),cFile.getOriginalFilename());
        List<Image> resultImages = imageRepository.findAll();
        assertThat(resultImages.size()).isEqualTo(2);
    }

    void clear(){
        em.flush();
        em.clear();
    }

}

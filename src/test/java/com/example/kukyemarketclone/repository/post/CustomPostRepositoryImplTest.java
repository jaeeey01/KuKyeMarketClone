package com.example.kukyemarketclone.repository.post;

import com.example.kukyemarketclone.config.QuerydslConfig;
import com.example.kukyemarketclone.dto.post.PostReadCondition;
import com.example.kukyemarketclone.dto.post.PostSimpleDto;
import com.example.kukyemarketclone.entity.category.Category;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.post.Post;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import java.util.stream.IntStream;

import static com.example.kukyemarketclone.factory.dto.PostReadConditionFactory.createPostReadCondition;
import static com.example.kukyemarketclone.factory.entity.CategoryFactory.createCategoryWithName;
import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;
import static com.example.kukyemarketclone.factory.entity.PostFactory.createPost;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QuerydslConfig.class)
class CustomPostRepositoryImplTest {
    /*@Import(QuerydslConfig.class)
    * @DataJpaTest는 JPA와 관련된 빈만 등록해주기 때문에
    * QuerydslConfig에서 직접 정의했던 JPAQuesyFactory빈은 등록 해주지 않음
    *  = @DataJpaTest로 테스트하는 클래스들은 @Import()로 선언 해야 빈 등록 가눙
    * */


    @Autowired PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void findAllByConditionTest() { //총 페이지수, 총 게시글 수, 다음 페이지 여부 등
        //given
        List<Member> members = saveMember(3);
        List<Category> categories =saveCategory(2);

        // 0 - (m0, c0)
        // 1 - (m1, c1) * 생성된 게시물
        // 2 - (m2, c0)
        // 3 - (m0, c1)
        // 4 - (m1, c0)
        // 5 - (m2, c1) * 생성된 게시물
        // 6 - (m0, c0)
        // 7 - (m1, c1) * 생성된 게시물
        // 8 - (m2, c0)
        // 9 - (m0, c1)
      List<Post> posts = IntStream.range(0,10)
              .mapToObj( i -> postRepository.save(createPost(members.get(i % 3), categories.get(i % 2))))
              .collect(toList());
      clear();

      List<Long> categoryIds = List.of(categories.get(1).getId());
      List<Long> memberIds = List.of(members.get(0).getId(),members.get(2).getId());
      int sizePerPage = 2;
      long expectedTotalElements = 3;

        PostReadCondition page0Cond = createPostReadCondition(0,sizePerPage,categoryIds,memberIds);
        PostReadCondition page1Cond = createPostReadCondition(1,sizePerPage,categoryIds,memberIds);

        //when
        Page<PostSimpleDto> page0 = postRepository.findAllByCondition(page0Cond);
        Page<PostSimpleDto> page1 = postRepository.findAllByCondition(page1Cond);

        //then
        assertThat(page0.getTotalElements()).isEqualTo(expectedTotalElements);//총 게시물 수
        assertThat(page0.getTotalPages()).isEqualTo((expectedTotalElements+1)/sizePerPage);//총 페이지 수

        assertThat(page0.getContent().size()).isEqualTo(2); // 0page 게시물 수
        assertThat(page1.getContent().size()).isEqualTo(1);// 1page 게시물 수

        // 9 - (m0, c1)
        // 5 - (m2, c1) * 생성된 게시물
        assertThat(page0.getContent().get(0).getId()).isEqualTo(posts.get(9).getId());
        assertThat(page0.getContent().get(1).getId()).isEqualTo(posts.get(5).getId());
        assertThat(page0.hasNext()).isTrue();//다음페이지 여부

        // 3 - (m0, c1)
        assertThat(page1.getContent().get(0).getId()).isEqualTo(posts.get(3).getId());
        assertThat(page1.hasNext()).isFalse();


    }

    private List<Member> saveMember(int size){
     return IntStream.range(0,size)
             .mapToObj(i -> memberRepository.save(createMember("member"+i, "member"+i,"member"+i,"member"+i)))
             .collect(toList());
    }

    private List<Category> saveCategory(int size){
        return IntStream.range(0, size)
                .mapToObj(i -> categoryRepository.save(createCategoryWithName("category"+i))).collect(toList());
    }

    private void clear(){
        em.flush();
        em.clear();
    }

}
package com.example.kukyemarketclone.repository.post;

import com.example.kukyemarketclone.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long>, CustomPostRepository {

    //각 게시글 조회시 작성자 정보도 같이 보내줄 것, fetch join을 이용 하여 member도 함께 조회
    @Query("SELECT p from Post p JOIN fetch p.member where p.id = :id")
    Optional<Post> findByIdWithMember(@Param("id") Long id);

    /*select
    post0_.id as id1_4_0_, member1_.member_id as member_i1_2_1_, post0_.created_at as created_2_4_0_, post0_.modified_at as modified3_4_0_, post0_.category_id as category7_4_0_, post0_.content as content4_4_0_, post0_.member_id as member_i8_4_0_, post0_.price as price5_4_0_, post0_.title as title6_4_0_,
    member1_.created_at as created_2_2_1_, member1_.modified_at as modified3_2_1_, member1_.email as email4_2_1_, member1_.nickname as nickname5_2_1_, member1_.password as password6_2_1_, member1_.username as username7_2_1_
    from
    post post0_
    inner
    join member member1_
    on post0_.member_id=member1_.member_id
    where
    post0_.id=?*/
}

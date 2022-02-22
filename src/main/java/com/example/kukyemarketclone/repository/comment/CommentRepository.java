package com.example.kukyemarketclone.repository.comment;

import com.example.kukyemarketclone.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    //댓글의 id 조회하면서 자신의 부모와 fetch join 된 결과 반환
    @Query("SELECT c FROM Comment c left join fetch c.parent where c.id = :id")
    Optional<Comment> findWithParentById(Long id);

    //부모의 아이디로 Null 우선적인 오름차순 정렬 , 그 다음 자신의 아이디로 오름 차순 조회
    @Query("SELECT c from Comment c join fetch c.member left join fetch c.parent where c.post.id = :postId order by c.parent.id asc  nulls first, c.id asc")
    List<Comment> findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(Long postId);
}

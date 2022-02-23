package com.example.kukyemarketclone.entity.comment;

import com.example.kukyemarketclone.entity.common.EntityDate;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.post.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Comment extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false)
    private boolean deleted; // 삭제여부 표시, 삭제 했더라도 아직 하위 댓글이 있어서 실제로 남겨둬야한다면, deleted는 true

    //  @OnDelete(action = OnDeleteAction.CASCADE)
    // Member, Category, 상위 Comment제거 시 연쇄적으로 현재 댓글 제거
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parent;

    @OneToMany(mappedBy = "parent") // 각 댓글의 하위 댓글들을 참조
    private List<Comment> children = new ArrayList<>();

    public Comment(String content, Member member, Post post, Comment parent){
        this.content = content;
        this.member = member;
        this.post = post;
        this.parent = parent;
        this.deleted = false;
    }

    /* findDeletableComment
    * 현재 댓글 기준, 실제로 삭제 할 수 있는 댓글 찾아줌
    * 이 메소드의 결과로 찾아낸 댓글은 실제 DB에서 제거해도 무방
    * 이 메소드의 결과로 찾아낸 댓글이 없다면 아직 하위댓글 존재 = 제거 대상 X
    *  = delete 메소드로 삭제 표시만 해야 함
    *  현재 댓글에 하위 댓글이 없으면 실제로 제거 해도 되는 댓글 찾기 위해 상위 댓글로 거슬러 올라가며 검사
    * */
    public Optional<Comment> findDeletableComment(){
        return hasChildren() ? Optional.empty() : Optional.of(findDeletableCommentByParent());
    }

    public void delete(){//실제 삭제가 아닌 삭제 표시만 해야한다면 true
        this.deleted = true;//하위 댓글이 남아있어서 실제로 제거할 수 없는 댓글인 경우 이 메소드 호출
    }

    /* findDeletableCommentByParent
    * 삭제되어 있는 최상위 부모까지 모두 올라 간 뒤, 해당 지점 부터 점차 내려오면서
    * 자신의 부모가 삭제가능한지(자식 갯수가 1인지) 확인
    * 삭제 할 수 있다면, 반환 받았던 상위 댓글 그대로 응답
    * 삭제 할 수 없다면, 자신을 반환
    *  = 모든 parent를 다 조회 해둔 뒤에 해당 parent의 children을 조회 하므로
    *  이런  children 조회 작업은, IN 절을 이용하여 하나의 쿼리로 수행 가능
    *
    * 자식의 개수를 뒤늦게 판별하기 때문에 상위 댓글로 올라가는 깊이는 늘어날 수 있지만
    * 1개의 깊이마다 2개의 쿼리가 생성되는 상황을, 1개의 쿼리만 생성되도록 개선 = 2 * N -> N
   * */
    private Comment findDeletableCommentByParent(){
        if(isDeletedParent()){
            Comment deletableParent = getParent().findDeletableCommentByParent();
            if(getParent().getChildren().size()==1) return deletableParent;
        }
        return this;
    }

    private boolean hasChildren(){//하위 댓글 있는지 판별
        return getChildren().size() != 0;
    }

    //deleted 가 true 이거나 null 체크
    private boolean isDeletedParent(){
        return getParent() != null && getParent().isDeleted();
    }

    /* jpa 에서 엔티티 조회시
    * 작성한 엔티티를 상속받은 프록시를 만들어서 사용
    * 만약 this.parent나 this.children 으로 참조 한다면 실제 DB에 저장되어 있지만
    * fetch 전략을 LAZY이기에 실제 데이터 불러오기 불가
    *  = 아직 조회되지 않은 데이터들을 적절한 시기에 불러 올 수 있도록
    * getChildren(), getParent()의 형태로 작성
    * */

}

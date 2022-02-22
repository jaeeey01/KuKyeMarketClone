package com.example.kukyemarketclone.repository.post;

import com.example.kukyemarketclone.dto.post.PostReadCondition;
import com.example.kukyemarketclone.dto.post.PostSimpleDto;
import com.example.kukyemarketclone.entity.post.Post;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static com.example.kukyemarketclone.entity.post.QPost.post;
import static com.querydsl.core.types.Projections.constructor;

@Transactional(readOnly = true)
public class CustomPostRepositoryImpl extends QuerydslRepositorySupport implements CustomPostRepository {
    /*QuerydslRepositorySupport
    * 페이징 처리를 위해 상속 받음
    * 여기에 정의된 메소드를 이용하면 빌드된 쿼리에 손쉽게 페이지 적용 가능
    * 상위 클래스에 이미 @Repository가 정의 되어있으므로, 하위클래스에는 별도로 정의 X
    * */


    /*private final JPAQueryFactory + CustomPostRepositoryImpl
    * 쿼리를 빌드하기 위해 빈애 등록해두었던 JPAQueryFactory 주입 받음
    * 상위 클래스에 생성자가 하나뿐이라 직접 생성자를 정의 해줌
    * */
    private final JPAQueryFactory jpaQueryFactory;

    public CustomPostRepositoryImpl(JPAQueryFactory jpaQueryFactory){
        super(Post.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<PostSimpleDto> findAllByCondition(PostReadCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicate(cond);
        return new PageImpl<>(fetchAll(predicate,pageable), pageable, fetchCount(predicate));
    }

    /*fetchAll
    * 게시글 목록 결과를 PostSimpleDto로 반환
    *  QuerydslRepositorySupport에 정의된 getQuerydsl().applyPagination()을 이용하여 페이징이 적용된 쿼리 빌드
    *  Projections.constructor 이용시 DTO에 즉시 프로젝션된 결과가 조회 됨
    *  사용자 닉네임도 조회해야하므로 Member와 조인
    * */
    private List<PostSimpleDto> fetchAll(Predicate predicate, Pageable pageable) {
        return getQuerydsl().applyPagination(
                pageable,
                jpaQueryFactory
                        .select(constructor(PostSimpleDto.class, post.id, post.title, post.member.nickname, post.createdAt))
                        .from(post)
                        .join(post.member)
                        .where(predicate)
                        .orderBy(post.id.desc())
        ).fetch();
    }
    private Long fetchCount(Predicate predicate) {
        return jpaQueryFactory.select(post.count()).from(post).where(predicate).fetchOne();
    }
    private Predicate createPredicate(PostReadCondition cond) { //(같은 조건1-1 OR 같은조건1-2) AND (같은 조건2-1 OR 같은조건2-2)
        return new BooleanBuilder()
                .and(orConditionsByEqCategoryIds(cond.getCategoryId()))
                .and(orConditionsByEqMemberIds(cond.getMemberId()));
    }

    /*orConditionsByEq CategoryIds + MemberIds
    * 전달받은 카테고리 / 멤버 ID로 BooleanExpression 빌드하여 반환
    * OR 연산으로 묶어낼 value들과 각항마다 수행할 비교 연산을 orConditions의 인자로 전달
    * */
    private Predicate orConditionsByEqCategoryIds(List<Long> categoryIds) {
        return orConditions(categoryIds, post.category.id::eq);
    }

    private Predicate orConditionsByEqMemberIds(List<Long> memberIds) {
        return orConditions(memberIds,post.member.id::eq);
    }

    /*orConditions
    * orConditionsByEq CategoryIds + MemberIds의 메소드 중복을 제거하기 위해 정의한 메소드
    * 전달 받은 value들을 OR연산으로 묶어서 반환
    * */
    private <T> Predicate orConditions(List<T> values, Function<T, BooleanExpression> term){
        return values.stream()
                .map(term)
                .reduce(BooleanExpression::or)
                .orElse(null);
    }




}

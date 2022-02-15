package com.example.kukyemarketclone.repository.category;

import com.example.kukyemarketclone.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    //부모의 아이디로 오름차순 NULL 우선정렬, 그다음 자신의 아이디 오름 차순 정렬 조회
    @Query("SELECT c FROM Category c left join c.parent p ORDER BY p.id ASC NULLS FIRST, c.id ASC ")
    List<Category> findAllOrderByParentIdAscNullsFirstCategoryIdASC();
}

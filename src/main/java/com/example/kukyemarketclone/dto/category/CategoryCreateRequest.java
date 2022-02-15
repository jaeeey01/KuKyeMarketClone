package com.example.kukyemarketclone.dto.category;

import com.example.kukyemarketclone.entity.category.Category;
import com.example.kukyemarketclone.exception.CategoryNotFoundException;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateRequest {

    private String name;
    private Long parentId;

    public static Category toEntity(CategoryCreateRequest req, CategoryRepository categoryRepository) {
       return new Category(req.getName(), Optional.ofNullable(req.getParentId())
               .map(id -> categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new))
               .orElse(null));

    }

}

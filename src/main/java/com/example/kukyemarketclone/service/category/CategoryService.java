package com.example.kukyemarketclone.service.category;

import com.example.kukyemarketclone.dto.category.CategoryCreateRequest;
import com.example.kukyemarketclone.dto.category.CategoryDto;
import com.example.kukyemarketclone.entity.category.Category;
import com.example.kukyemarketclone.exception.CategoryNotFoundException;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> readAll(){
        List<Category> categories = categoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdASC();
        return CategoryDto.toDtoList(categories);

    }

    @Transactional
    public void create(CategoryCreateRequest req){
        categoryRepository.save(CategoryCreateRequest.toEntity(req,categoryRepository));
    }

    @Transactional
    public void delete(Long id){
        if(notExistsCategory(id)) throw new CategoryNotFoundException();
        categoryRepository.deleteById(id);
    }

    private boolean notExistsCategory(Long id){
        return !categoryRepository.existsById(id);
    }

}

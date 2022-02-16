package com.example.kukyemarketclone.service.category;

import com.example.kukyemarketclone.dto.category.CategoryCreateRequest;
import com.example.kukyemarketclone.dto.category.CategoryDto;
import com.example.kukyemarketclone.exception.CategoryNotFoundException;
import com.example.kukyemarketclone.factory.entity.CategoryFactory;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.example.kukyemarketclone.factory.dto.CategoryCreateRequestFactory.createCategoryCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks CategoryService categoryService;
    @Mock
    CategoryRepository categoryRepository;

    @Test
    void readAllTest(){
        //given
        given(categoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdASC())
                .willReturn(
                        List.of(CategoryFactory.createCategoryWithName("name1"),
                                CategoryFactory.createCategoryWithName("name2")

                        )
                );

        //when
        List<CategoryDto> result = categoryService.readAll();

        //then

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("name1");
        assertThat(result.get(1).getName()).isEqualTo("name2");
    }

    @Test
    void createTest(){
        //given
        CategoryCreateRequest req = createCategoryCreateRequest();

        //when
        categoryService.create(req);

        //then
        verify(categoryRepository).save(any());
    }

    @Test
    void deleteTest(){
        //given
        given(categoryRepository.existsById(anyLong())).willReturn(true);

        //when
        categoryService.delete(1L);

        //then
        verify(categoryRepository).deleteById(anyLong());
    }

    @Test
    void deleteExceptionByCategoryNotFoundTest(){
        //given
        given(categoryRepository.existsById(anyLong())).willReturn(false);

        //when, then
        assertThatThrownBy( ()-> categoryService.delete(1L)).isInstanceOf(CategoryNotFoundException.class);
    }

}

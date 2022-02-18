package com.example.kukyemarketclone.dto.category;

import com.example.kukyemarketclone.entity.category.Category;
import com.example.kukyemarketclone.helper.NestedConvertHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private List<CategoryDto> children;

    //categories를 인자로 받아 계층형 구조로 변환하여 반환
    //NestedConvertHelper클래스를 통해 카테고리르 중첩구조로 변환
    public static List<CategoryDto> toDtoList(List<Category> categories){
        NestedConvertHelper helper = NestedConvertHelper.newInstance(
                categories, //계층형 구조로 변환할 엔티티 목록
                c -> new CategoryDto(c.getId(),c.getName(),new ArrayList<>()),//엔티티를 DTO로 변환하는 함수
                c -> c.getParent(), //엔티티의 부모를 반환하는 함수
                c -> c.getId(), //엔티티의 ID를 반환하는 함수
                d -> d.getChildren());  //DTO의 자식목록을 반환하는 함수
        return helper.convert();    //계층형 구조의 CategoryDto리스트 반환
    }
}

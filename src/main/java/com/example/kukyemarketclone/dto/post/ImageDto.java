package com.example.kukyemarketclone.dto.post;

import com.example.kukyemarketclone.entity.post.Image;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageDto {
    private Long id;
    private String originName;
    private String uniqueName;
    public static ImageDto toDto(Image image){//원래의 파일명과 서버에서 생성한 고유 파일명으로 DTO 생성, 반환
        return new ImageDto(image.getId(),image.getOriginName(),image.getUniqueName());
    }
}

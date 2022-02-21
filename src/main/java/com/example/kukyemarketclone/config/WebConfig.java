package com.example.kukyemarketclone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@EnableWebMvc//Swagger 사용 하기 위해서 추가
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.image.location}")
    private String location;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/image/**")//url에 /image/ 접두 경로 설정시
                .addResourceLocations("file:"+location)//파일 시스템의 location 경로에서 파일 접근
                //이미지는 고유의이름 가지고 있음 수정 x -> 캐시설정
                //자원 접근시 새롭게 내려받지 않고 캐시된 자원 이용, 1시간 이후 캐시만료 -> 재요청
                .setCacheControl(CacheControl.maxAge(Duration.ofHours(1L)).cachePublic());
    }


}

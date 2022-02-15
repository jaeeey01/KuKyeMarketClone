package com.example.kukyemarketclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

//스프링부트에서 작성했던 bean validation을 문서화 하기 위함
//@Valid가 선언된 DTO 객체들의 bean validation 조건을 문서화 가능
@Import(BeanValidatorPluginsConfiguration.class)
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())//api 문서 정보 작성
                .select()//API문서를 작성할 셀렉터 지정
                    .apis(RequestHandlerSelectors.basePackage("com.example.kukyemarketclone.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(List.of(apiKey()))//요청에 포함되어야 할 Authorization 헤더를
                .securityContexts(List.of(securityContext()));//전역적으로 지정
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("KUKE market clone")
                .description("KUKE market clone REST API Documentation")
                .license("yjh920127@gmail.com")
                .licenseUrl("https://github.com/yjh920127/KuKyeMarketClone")
                .version("1.0")
                .build();
    }

    private static ApiKey apiKey(){
        return new ApiKey("Authorization","Bearer Token","header");
    }

    private SecurityContext securityContext(){
        return SecurityContext.builder().securityReferences(defaultAuth())
                .operationSelector(oc -> oc.requestMappingPattern().startsWith("/api/")).build();
    }

    private List<SecurityReference> defaultAuth(){
        AuthorizationScope authorizationScope = new AuthorizationScope("global","global access");
        return List.of(new SecurityReference("Authorization", new AuthorizationScope[]{authorizationScope}));
    }
}

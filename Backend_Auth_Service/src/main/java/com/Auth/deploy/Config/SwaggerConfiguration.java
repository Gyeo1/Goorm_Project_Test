package com.Auth.deploy.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

//스웨거 api 사용을 위한설정
@Configuration
@EnableWebMvc
public class SwaggerConfiguration {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.Auth.deploy.Controller"))

                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Practice Swagger")
                .description("practice swagger config")
                .version("1.0")
                .build();
    }
//    public Docket swaggerApi() {
//        return new Docket(DocumentationType.OAS_30).apiInfo(swaggerInfo()).select()
//                .apis(RequestHandlerSelectors.basePackage("com.example.firstproject.controller"))// 컨트롤러의 위치를 설정 해당 컨트롤러의 내용에서 @API 매핑된 내용을 문서화
//                .paths(PathSelectors.any()) //원하는 이후 경로를 추가 가능.
//                .build()
//                .useDefaultResponseMessages(false); // 기본으로 세팅되는 200,401,403,404 메시지를 표시 하지 않음
//    }
//
//    private ApiInfo swaggerInfo() {
//        return new ApiInfoBuilder().title("Spring API Documentation")
//                .description("앱 개발시 사용되는 서버 API에 대한 연동 문서입니다")
//                .version("1.0")
//                .build();
//    }
}
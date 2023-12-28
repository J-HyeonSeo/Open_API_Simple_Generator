package com.jhsfully.api.configuration;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

  @Bean
  public Docket api(){
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .securityContexts(Arrays.asList(securityContext("Authorization")))
        .securitySchemes(Arrays.asList(apiKey("Authorization")))
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.jhsfully.api"))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo apiInfo(){
    return new ApiInfoBuilder()
        .title("OpenAPI 간단 구축 서비스")
        .description("유저가 엑셀파일을 업로드하여, OpenAPI형식으로 데이터를 받을 수 있게 도와주는 API구축 서비스")
        .version("1.0.0")
        .build();
  }

  private ApiKey apiKey(String name) {
    return new ApiKey(name, name, "header");
  }

  //전역 AuthorizationScope를 사용하여 JWT SecurityContext를 구성.
  private SecurityContext securityContext(String name) {
    return SecurityContext.builder()
        .securityReferences(defaultAuth(name))
        .build();
  }

  private List<SecurityReference> defaultAuth(String name) {
    AuthorizationScope authorizationScope =
        new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes =
        new AuthorizationScope[1];

    authorizationScopes[0] = authorizationScope;
    return Arrays.asList(
        new SecurityReference(name, authorizationScopes));
  }

}
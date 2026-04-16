package com.smartcare.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("智慧养老系统API")
                        .version("1.0")
                        .description("智慧养老系统接口文档")
        );
    }

    @Bean
    public GroupedOpenApi userAPI() {
        return GroupedOpenApi.builder()
                .group("用户管理")
                .pathsToMatch(
                        "/api/user/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi allAPI() {
        return GroupedOpenApi.builder()
                .group("全部接口")
                .pathsToMatch(
                        "/**"
                )
                .build();
    }
}
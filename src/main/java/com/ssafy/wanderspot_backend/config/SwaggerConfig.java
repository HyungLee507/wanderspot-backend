package com.ssafy.wanderspot_backend.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "Wander Spot API 명세서",
                description = "<h3>Wander Spot API Reference for Developers</h3>Swagger를 이용한 Wander Spot API<br><img src=\"/img/ssafy_logo.png\" width=\"150\">",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("SSAFY VUE API 명세서")
                .description(
                        "<h3>SSAFY API Reference for Developers</h3>Swagger를 이용한 VUE API<br><img src=\"/assets/img/ssafy_logo.png\" width=\"150\">")
                .version("v1")
                .contact(new Contact()
                        .name("hissam")
                        .email("hissam@ssafy.com")
                        .url("http://edu.ssafy.com"));

        return new OpenAPI().components(new Components()).info(info);
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder().group("Wander Spot-all").pathsToMatch("/**").build();
    }

    @Bean
    public GroupedOpenApi boardApi() {
        return GroupedOpenApi.builder().group("Wander Spot-board").pathsToMatch("/board/**").build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder().group("Wander Spot-user").pathsToMatch("/user/**").build();
    }

    @Bean
    public GroupedOpenApi mapApi() {
        return GroupedOpenApi.builder().group("Wander Spot-map").pathsToMatch("/map/**").build();
    }


}

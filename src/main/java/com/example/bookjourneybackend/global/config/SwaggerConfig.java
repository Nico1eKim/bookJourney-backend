package com.example.bookjourneybackend.global.config;

import com.example.bookjourneybackend.global.annotation.LoginUserId;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    /**
     * 🔥 @LoginUserId가 있는 API에 자동으로 @SecurityRequirement 추가
     */
    @Bean
    public OperationCustomizer customizeSecurity() {
        return (operation, handlerMethod) -> {
            if (handlerMethod != null) {
                boolean requiresAuth = Arrays.stream(((HandlerMethod) handlerMethod).getMethodParameters())
                        .anyMatch(param -> param.getParameterAnnotation(LoginUserId.class) != null);

                if (requiresAuth) {
                    operation.addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
                }
            }
            return operation;
        };
    }
}
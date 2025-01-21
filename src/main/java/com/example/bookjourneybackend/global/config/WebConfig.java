package com.example.bookjourneybackend.global.config;

import com.example.bookjourneybackend.global.resolver.GetBookListRequestArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final GetBookListRequestArgumentResolver getBookListRequestArgumentResolver;

    public WebConfig(GetBookListRequestArgumentResolver getBookListRequestArgumentResolver) {
        this.getBookListRequestArgumentResolver = getBookListRequestArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // Argument Resolver 등록
        resolvers.add(getBookListRequestArgumentResolver);
    }
}

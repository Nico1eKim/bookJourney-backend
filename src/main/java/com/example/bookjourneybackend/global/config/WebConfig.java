package com.example.bookjourneybackend.global.config;

import com.example.bookjourneybackend.global.resolver.GetBookListRequestArgumentResolver;
import com.example.bookjourneybackend.global.resolver.LoginUserResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final GetBookListRequestArgumentResolver getBookListRequestArgumentResolver;
    private final LoginUserResolver loginUserResolver;

    public WebConfig(GetBookListRequestArgumentResolver getBookListRequestArgumentResolver, LoginUserResolver loginUserResolver) {
        this.getBookListRequestArgumentResolver = getBookListRequestArgumentResolver;
        this.loginUserResolver = loginUserResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // Argument Resolver 등록
        resolvers.add(getBookListRequestArgumentResolver);
        resolvers.add(loginUserResolver);
    }
}

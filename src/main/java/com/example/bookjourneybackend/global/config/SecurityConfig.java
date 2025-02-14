package com.example.bookjourneybackend.global.config;

import com.example.bookjourneybackend.global.util.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint entryPoint;


    private static final String[] AUTH_WHITELIST = {
            "/swagger-ui/**", "/api-docs", "/swagger-ui-custom.html",
            "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html","/swagger-ui/index.html",
            "/auth/login","/auth/reissue",
            "/users/signup","/users/emails/verification-requests","/users/emails/verifications",
            "/users/nickname","/h2-console/**"

            , "/books/**", "/rooms/**", "/users/**","/**"    //개발을 위해 일시적으로 허용..,
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //CSRF, CORS
        httpSecurity.csrf(CsrfConfigurer<HttpSecurity>::disable)

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 요청 허용
                                .requestMatchers(AUTH_WHITELIST).permitAll()  // 화이트리스트 경로 허용
                                .anyRequest().authenticated()                 // 나머지는 인증 필요

                )
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint) // 커스텀 엔트리 포인트 등록
                )
                //세션 관리 상태 없음으로 구성, Spring Security가 세션 생성 or 사용 X
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .headers(headers ->
                        headers
                                .frameOptions().sameOrigin() // H2 콘솔을 허용하도록 프레임 옵션을 sameOrigin으로 설정
                                .contentSecurityPolicy("frame-ancestors 'self'") // X-Frame-Options 대체
                );

        return httpSecurity.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",  // 로컬 환경
                "http://ec2-13-48-61-179.eu-north-1.compute.amazonaws.com",  // 서버 배포 환경
                "https://book-journey-two.vercel.app", //프엔 배포 환경
                "https://book-journey.click"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE","PUT","OPTIONS"));  // 허용할 HTTP 메서드 설정
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type","Refresh-Token"));  // 허용할 헤더 설정
        configuration.setAllowCredentials(true);  // 자격 증명 허용 설정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}

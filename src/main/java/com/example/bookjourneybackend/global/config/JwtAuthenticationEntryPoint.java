package com.example.bookjourneybackend.global.config;


import com.example.bookjourneybackend.global.response.BaseErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Autowired
    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");

        // BaseErrorResponse 생성
        BaseErrorResponse baseErrorResponse = new BaseErrorResponse(EXPIRED_TOKEN.getCode(),EXPIRED_TOKEN.getMessage());

        String result = objectMapper.writeValueAsString(baseErrorResponse);

        // JSON 응답 전송
        response.getWriter().write(result);
    }
}
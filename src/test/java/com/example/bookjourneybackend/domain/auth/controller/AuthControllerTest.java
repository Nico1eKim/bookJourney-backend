package com.example.bookjourneybackend.domain.auth.controller;

import com.example.bookjourneybackend.domain.auth.domain.dto.request.PostAuthLoginRequest;
import com.example.bookjourneybackend.domain.auth.domain.dto.response.PostAuthLoginResponse;
import com.example.bookjourneybackend.domain.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AuthController authController;

    @MockBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("로그인 컨트롤러 테스트")
    void 로그인_컨트롤러_테스트() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        PostAuthLoginRequest postAuthLoginRequest = new PostAuthLoginRequest("test@example.com", "password123");
        PostAuthLoginResponse postAuthLoginResponse = PostAuthLoginResponse.of(1L, "accessToken", "refreshToken");

        when(authService.login(eq(postAuthLoginRequest), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(postAuthLoginResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postAuthLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"));

    }

//    @Test
//    @DisplayName("엑세스 토큰 재발급 컨트롤러 테스트")
//    void 엑세스_토큰_재발급_컨트롤러_테스트() throws Exception {
//        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
//
//        PostAuthAccessTokenReissueRequest request = new PostAuthAccessTokenReissueRequest("validToken");
//        PostAuthAccessTokenReissueResponse response = PostAuthAccessTokenReissueResponse.of("newAccessToken");
//
//        when(authService.tokenReissue(any(), any(), any())).thenReturn(response);
//
//        mockMvc.perform(post("/reissue")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"refreshToken\":\"validToken\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.accessToken").value("newAccessToken"));
//
//        verify(authService).tokenReissue(any(), any(), any());
//    }
//
//    @Test
//    @DisplayName("로그아웃 컨트롤러 테스트")
//    void 로그아웃_컨트롤러_테스트() throws Exception {
//        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
//
//        doNothing().when(authService).logout(anyLong());
//
//        mockMvc.perform(post("/logout")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("userId", "1"))
//                .andExpect(status().isOk());
//
//        verify(authService).logout(anyLong());
//    }
//}
}
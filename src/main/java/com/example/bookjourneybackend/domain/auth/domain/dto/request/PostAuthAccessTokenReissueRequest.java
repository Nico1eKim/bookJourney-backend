package com.example.bookjourneybackend.domain.auth.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PostAuthAccessTokenReissueRequest {

    //엑세스 토큰 재발급 Request
    @NotNull(message = "리프레쉬 토큰 입력은 필수입니다")
    private String refreshToken;

}

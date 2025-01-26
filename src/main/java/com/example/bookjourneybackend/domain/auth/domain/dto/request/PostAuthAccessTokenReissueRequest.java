package com.example.bookjourneybackend.domain.auth.domain.dto.request;

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
    private String refreshToken;

}
